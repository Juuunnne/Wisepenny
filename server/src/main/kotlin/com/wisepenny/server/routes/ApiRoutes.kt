package com.wisepenny.server.routes

import com.wisepenny.server.dto.TokenRequest
import com.wisepenny.server.dto.TransferRequest
import com.wisepenny.server.plugins.JWT_AUTH
import com.wisepenny.server.plugins.UnauthorizedException
import com.wisepenny.server.plugins.ValidationException
import com.wisepenny.server.service.AccountService
import com.wisepenny.server.service.AuthService
import com.wisepenny.server.service.BalanceService
import com.wisepenny.server.service.TransferService
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import java.util.UUID

/** The authenticated user id, taken from the verified token's subject. */
private fun ApplicationCall.userId(): UUID {
    val principal = principal<JWTPrincipal>() ?: throw UnauthorizedException("Missing token")
    val subject = principal.subject ?: throw UnauthorizedException("Invalid token")
    return UUID.fromString(subject)
}

/** Public: exchange credentials for a token. Everything else sits behind [authenticate]. */
fun Route.authRoutes(authService: AuthService) {
    post("/auth/token") {
        val body = call.receive<TokenRequest>()
        call.respond(authService.authenticate(body.email, body.password))
    }
}

fun Route.accountRoutes(accountService: AccountService) {
    authenticate(JWT_AUTH) {
        get("/accounts") {
            call.respond(accountService.listAccounts(call.userId()))
        }

        get("/accounts/{id}/transactions") {
            val userId = call.userId()
            val accountId = parseUuidParam(call.parameters["id"])
            val page = call.request.queryParameters["page"]
                .parseIntOrDefault(default = 0, name = "page") { it >= 0 }
            val size = call.request.queryParameters["size"]
                .parseIntOrDefault(default = 20, name = "size") { it in 1..100 }
            call.respond(accountService.transactions(userId, accountId, page, size))
        }
    }
}

fun Route.balanceRoutes(balanceService: BalanceService) {
    authenticate(JWT_AUTH) {
        get("/balances") {
            call.respond(balanceService.balances(call.userId()))
        }
    }
}

fun Route.savingsRoutes(transferService: TransferService) {
    authenticate(JWT_AUTH) {
        post("/savings/transfer") {
            val userId = call.userId()
            val body = call.receive<TransferRequest>()
            call.respond(transferService.transfer(userId, body))
        }
    }
}

// --- small input helpers: malformed input → 422 ---

private fun parseUuidParam(raw: String?): UUID =
    try {
        UUID.fromString(raw ?: throw ValidationException("Missing account id"))
    } catch (_: IllegalArgumentException) {
        throw ValidationException("Account id must be a valid UUID")
    }

private fun String?.parseIntOrDefault(default: Int, name: String, valid: (Int) -> Boolean): Int {
    if (this == null) return default
    val value = toIntOrNull()?.takeIf(valid)
        ?: throw ValidationException("$name is out of range")
    return value
}
