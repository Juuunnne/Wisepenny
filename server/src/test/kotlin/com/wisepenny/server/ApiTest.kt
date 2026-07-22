package com.wisepenny.server

import com.wisepenny.server.dto.BalancesDto
import com.wisepenny.server.dto.PageDto
import com.wisepenny.server.dto.TokenRequest
import com.wisepenny.server.dto.TokenResponse
import com.wisepenny.server.dto.TransferResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.MapApplicationConfig
import kotlinx.serialization.json.Json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * End-to-end tests over the real HTTP stack (Ktor testApplication) against a real
 * Postgres (Testcontainers). Each test resets the database, then relies on the dev
 * seeder to recreate the known demo dataset, so tests are independent and repeatable.
 */
class ApiTest {

    // 1 — happy-path authentication
    @Test
    fun `auth with valid credentials returns a token`() = apiTest {
        val client = jsonClient()
        val response = client.post("/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(TokenRequest("demo@wisepenny.fr", "demo1234"))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<TokenResponse>()
        assertTrue(body.accessToken.isNotBlank())
        assertEquals(3600, body.expiresIn)
    }

    // 2 — wrong password must not authenticate
    @Test
    fun `auth with wrong password returns 401`() = apiTest {
        val response = jsonClient().post("/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(TokenRequest("demo@wisepenny.fr", "wrong-password"))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    // 3 — protected route without a token
    @Test
    fun `accounts without a token returns 401`() = apiTest {
        val response = jsonClient().get("/accounts")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    // 4 — a user sees only their own accounts
    @Test
    fun `accounts with a token returns the callers accounts`() = apiTest {
        val client = jsonClient()
        val token = client.login()
        val response = client.get("/accounts") { bearer(token) }
        assertEquals(HttpStatusCode.OK, response.status)
        val accounts = response.body<List<AccountView>>()
        assertEquals(2, accounts.size)
        assertTrue(accounts.any { it.label == "Compte courant" })
    }

    // 5 — OWASP A01: reading another user's account must look like it does not exist (404, not 403)
    @Test
    fun `transactions on another users account returns 404`() = apiTest {
        val client = jsonClient()
        val token = client.login() // demo user
        val other = TestDb.insertUserWithAccount("mallory@wisepenny.fr")

        val response = client.get("/accounts/${other.accountId}/transactions") { bearer(token) }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    // 6 — pagination is real and coherent
    @Test
    fun `transactions pagination is coherent`() = apiTest {
        val client = jsonClient()
        val token = client.login()
        val accountId = client.get("/accounts") { bearer(token) }.body<List<AccountView>>().first().id

        val page0 = client.get("/accounts/$accountId/transactions?page=0&size=5") { bearer(token) }.body<PageDto>()
        val page1 = client.get("/accounts/$accountId/transactions?page=1&size=5") { bearer(token) }.body<PageDto>()

        assertEquals(5, page0.items.size)
        assertTrue(page0.total > 5)
        // Different pages return different rows.
        assertTrue(page0.items.map { it.id }.intersect(page1.items.map { it.id }.toSet()).isEmpty())
    }

    // 7 — negative transfer amount is rejected
    @Test
    fun `transfer with a negative amount returns 422`() = apiTest {
        val client = jsonClient()
        val token = client.login()
        val accountId = client.get("/accounts") { bearer(token) }.body<List<AccountView>>().first().id
        val goalId = TestDb.firstGoalId()

        val response = client.post("/savings/transfer") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(TransferBody(accountId, "-10.00", goalId.toString()))
        }
        assertEquals(HttpStatusCode.UnprocessableEntity, response.status)
    }

    // 8 — a valid transfer decrements the source balance by exactly the amount
    @Test
    fun `transfer with a valid amount decrements the balance`() = apiTest {
        val client = jsonClient()
        val token = client.login()

        val current = client.get("/accounts") { bearer(token) }.body<List<AccountView>>()
            .first { it.label == "Compte courant" }
        val before = BigDecimal(current.balance)
        val goalId = TestDb.firstGoalId()

        val response = client.post("/savings/transfer") {
            bearer(token)
            contentType(ContentType.Application.Json)
            setBody(TransferBody(current.id, "50.00", goalId.toString()))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<TransferResponse>()
        assertEquals(before - BigDecimal("50.00"), BigDecimal(result.newBalance))

        // And the change is reflected in /balances.
        val balances = client.get("/balances") { bearer(token) }.body<BalancesDto>()
        val newBalance = balances.byAccount.first { it.accountId == current.id }.balance
        assertEquals(before - BigDecimal("50.00"), BigDecimal(newBalance))
    }

    // ------------------------------------------------------------------
    // Test harness helpers
    // ------------------------------------------------------------------

    /** Resets the DB, then runs the block against a freshly configured app. */
    private fun apiTest(block: suspend ApplicationTestBuilder.() -> Unit) {
        TestDb.reset()
        testApplication {
            environment {
                config = MapApplicationConfig(
                    "app.environment" to "dev", // triggers the demo seeder
                    "jwt.secret" to "test-secret-not-for-production",
                    "jwt.issuer" to "wisepenny",
                    "jwt.audience" to "wisepenny-app",
                    "jwt.realm" to "wisepenny-api",
                    "jwt.expiresInSeconds" to "3600",
                    "db.jdbcUrl" to TestDb.container.jdbcUrl,
                    "db.user" to TestDb.container.username,
                    "db.password" to TestDb.container.password,
                    "db.driver" to "org.postgresql.Driver",
                )
            }
            application { module() }
            block()
        }
    }

    private fun ApplicationTestBuilder.jsonClient(): HttpClient = createClient {
        // Our client views only assert on a subset of fields, so tolerate the rest.
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }

    private suspend fun HttpClient.login(
        email: String = "demo@wisepenny.fr",
        password: String = "demo1234",
    ): String {
        val response = post("/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(TokenRequest(email, password))
        }
        return response.body<TokenResponse>().accessToken
    }
}

// Minimal client-side views (the server DTOs' fields we assert on).
@kotlinx.serialization.Serializable
private data class AccountView(val id: String, val label: String, val balance: String)

@kotlinx.serialization.Serializable
private data class TransferBody(val fromAccountId: String, val amount: String, val goalId: String)

private fun io.ktor.client.request.HttpRequestBuilder.bearer(token: String) =
    header(HttpHeaders.Authorization, "Bearer $token")
