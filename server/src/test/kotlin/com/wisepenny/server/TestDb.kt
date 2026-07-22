package com.wisepenny.server

import com.wisepenny.server.db.Accounts
import com.wisepenny.server.db.SavingsGoals
import com.wisepenny.server.db.Transactions
import com.wisepenny.server.db.Transfers
import com.wisepenny.server.db.Users
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * A single throwaway PostgreSQL, started once for the whole test run via Testcontainers
 * (real Postgres — not H2 — so the tests exercise the same SQL dialect as production).
 * The container is torn down automatically by Testcontainers' reaper at JVM exit.
 */
object TestDb {

    val container: PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>("postgres:16-alpine").apply {
        withDatabaseName("wisepenny_test")
        withUsername("test")
        withPassword("test")
        start()
    }

    /** A test-owned connection used only to arrange/inspect data around each test. */
    private val db: Database by lazy {
        Database.connect(
            url = container.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = container.username,
            password = container.password,
        )
    }

    /** Empties every table so each test starts from a known, seed-only state. */
    fun reset() = transaction(db) {
        SchemaUtils.create(Users, Accounts, Transactions, SavingsGoals, Transfers)
        exec("TRUNCATE transfers, transactions, savings_goals, accounts, users RESTART IDENTITY CASCADE")
    }

    /** Adds a second user with one account — used to prove cross-user isolation (OWASP A01). */
    fun insertUserWithAccount(email: String): AccountRef = transaction(db) {
        val userId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        Users.insert {
            it[id] = userId
            it[Users.email] = email
            it[passwordHash] = "not-used-in-this-test"
            it[createdAt] = LocalDateTime.now()
        }
        Accounts.insert {
            it[id] = accountId
            it[Accounts.userId] = userId
            it[label] = "Compte tiers"
            it[ibanMasked] = "FR76 •••• •••• •••• 0000"
            it[balance] = BigDecimal("10.00")
            it[currency] = "EUR"
        }
        AccountRef(userId, accountId)
    }

    /** The demo user's first savings goal id (there is no public endpoint that exposes it). */
    fun firstGoalId(): UUID = transaction(db) {
        SavingsGoals.selectAll().orderBy(SavingsGoals.label to SortOrder.ASC).first()[SavingsGoals.id]
    }

    data class AccountRef(val userId: UUID, val accountId: UUID)
}
