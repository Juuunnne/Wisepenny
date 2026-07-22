package com.wisepenny.server.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.wisepenny.server.db.Accounts
import com.wisepenny.server.db.SavingsGoals
import com.wisepenny.server.db.Transactions
import com.wisepenny.server.db.Transfers
import com.wisepenny.server.db.Users
import com.wisepenny.server.dto.AccountDto
import com.wisepenny.server.dto.BalanceByAccountDto
import com.wisepenny.server.dto.BalancesDto
import com.wisepenny.server.dto.PageDto
import com.wisepenny.server.dto.TokenResponse
import com.wisepenny.server.dto.TransactionDto
import com.wisepenny.server.dto.TransferRequest
import com.wisepenny.server.dto.TransferResponse
import com.wisepenny.server.plugins.NotFoundException
import com.wisepenny.server.plugins.UnauthorizedException
import com.wisepenny.server.plugins.ValidationException
import com.wisepenny.server.security.JwtIssuer
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.UUID

// ---------------------------------------------------------------------------
// Auth
// ---------------------------------------------------------------------------

class AuthService(
    private val database: Database,
    private val jwtIssuer: JwtIssuer,
) {
    /** Verifies credentials against the BCrypt hash and returns a signed token. */
    fun authenticate(email: String, password: String): TokenResponse {
        val row = transaction(database) {
            Users.selectAll().where { Users.email eq email }.singleOrNull()
        } ?: throw UnauthorizedException("Invalid credentials")

        val verified = BCrypt.verifyer()
            .verify(password.toCharArray(), row[Users.passwordHash].toCharArray())
            .verified
        // Same error whether the email is unknown or the password is wrong — no user enumeration.
        if (!verified) throw UnauthorizedException("Invalid credentials")

        val (token, expiresIn) = jwtIssuer.issue(row[Users.id], email)
        return TokenResponse(token, expiresIn)
    }
}

// ---------------------------------------------------------------------------
// Accounts & transactions
// ---------------------------------------------------------------------------

class AccountService(private val database: Database) {

    fun listAccounts(userId: UUID): List<AccountDto> = transaction(database) {
        // OWASP A01 — scoped to the caller's user id; a user never sees others' accounts.
        Accounts.selectAll().where { Accounts.userId eq userId }.map { it.toAccountDto() }
    }

    fun transactions(userId: UUID, accountId: UUID, page: Int, size: Int): PageDto = transaction(database) {
        // OWASP A01 — confirm the account belongs to the caller BEFORE returning any data.
        // Respond 404 (not 403) so we don't reveal that the id exists to a non-owner.
        val owns = Accounts.selectAll()
            .where { Accounts.id eq accountId }
            .andWhere { Accounts.userId eq userId }
            .count() > 0
        if (!owns) throw NotFoundException("Account not found")

        val total = Transactions.selectAll()
            .where { Transactions.accountId eq accountId }
            .count()

        // Real DB-side pagination — LIMIT/OFFSET, never load-all-then-slice.
        val items = Transactions.selectAll()
            .where { Transactions.accountId eq accountId }
            .orderBy(Transactions.date to SortOrder.DESC)
            .limit(size)
            .offset(page.toLong() * size)
            .map { it.toTransactionDto() }

        PageDto(items = items, page = page, total = total)
    }
}

// ---------------------------------------------------------------------------
// Balances
// ---------------------------------------------------------------------------

class BalanceService(private val database: Database) {

    fun balances(userId: UUID): BalancesDto = transaction(database) {
        // OWASP A01 — only the caller's accounts contribute to the totals.
        val rows = Accounts.selectAll()
            .where { Accounts.userId eq userId }
            .map { it[Accounts.id] to it[Accounts.balance] }

        val total = rows.fold(BigDecimal.ZERO) { acc, (_, balance) -> acc + balance }
        BalancesDto(
            total = total.toPlainString(),
            byAccount = rows.map { (id, balance) -> BalanceByAccountDto(id.toString(), balance.toPlainString()) },
        )
    }
}

// ---------------------------------------------------------------------------
// Savings transfer
// ---------------------------------------------------------------------------

class TransferService(private val database: Database) {

    fun transfer(userId: UUID, req: TransferRequest): TransferResponse = transaction(database) {
        val amount = parseAmount(req.amount)
        val fromId = parseUuid(req.fromAccountId, "fromAccountId")
        val goalId = parseUuid(req.goalId, "goalId")

        // OWASP A01 — both the source account and the goal must belong to the caller.
        val account = Accounts.selectAll()
            .where { Accounts.id eq fromId }
            .andWhere { Accounts.userId eq userId }
            .singleOrNull() ?: throw NotFoundException("Account not found")

        val goal = SavingsGoals.selectAll()
            .where { SavingsGoals.id eq goalId }
            .andWhere { SavingsGoals.userId eq userId }
            .singleOrNull() ?: throw NotFoundException("Goal not found")

        val currentBalance = account[Accounts.balance]
        if (currentBalance < amount) throw ValidationException("Insufficient balance")

        val newBalance = currentBalance - amount
        val newGoalAmount = goal[SavingsGoals.currentAmount] + amount

        Accounts.update({ Accounts.id eq fromId }) { it[balance] = newBalance }
        SavingsGoals.update({ SavingsGoals.id eq goalId }) { it[currentAmount] = newGoalAmount }

        val transferId = UUID.randomUUID()
        Transfers.insert {
            it[id] = transferId
            it[fromAccountId] = fromId
            it[Transfers.goalId] = goalId
            it[Transfers.amount] = amount
            it[createdAt] = LocalDateTime.now()
        }

        TransferResponse(transferId = transferId.toString(), newBalance = newBalance.toPlainString())
    }
}

// ---------------------------------------------------------------------------
// Mappers & input validation (422 on bad input)
// ---------------------------------------------------------------------------

private fun ResultRow.toAccountDto() = AccountDto(
    id = this[Accounts.id].toString(),
    label = this[Accounts.label],
    ibanMasked = this[Accounts.ibanMasked],
    balance = this[Accounts.balance].toPlainString(),
    currency = this[Accounts.currency],
)

private fun ResultRow.toTransactionDto() = TransactionDto(
    id = this[Transactions.id].toString(),
    date = this[Transactions.date].toString(),
    label = this[Transactions.label],
    amount = this[Transactions.amount].toPlainString(),
    category = this[Transactions.category].name,
)

private fun parseAmount(raw: String): BigDecimal {
    val value = raw.toBigDecimalOrNull()?.setScale(2, RoundingMode.HALF_UP)
        ?: throw ValidationException("amount must be a valid number")
    if (value <= BigDecimal.ZERO) throw ValidationException("amount must be greater than 0")
    return value
}

private fun parseUuid(raw: String, field: String): UUID =
    try {
        UUID.fromString(raw)
    } catch (_: IllegalArgumentException) {
        throw ValidationException("$field must be a valid UUID")
    }
