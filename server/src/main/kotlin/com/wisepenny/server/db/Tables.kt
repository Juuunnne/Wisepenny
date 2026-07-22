package com.wisepenny.server.db

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.date
import org.jetbrains.exposed.v1.javatime.datetime

/**
 * Spending categories. Stored as the enum *name* via [enumerationByName]; any value
 * outside this set cannot be written, which is the app-level equivalent of a DB CHECK
 * constraint (see API_SPEC.md).
 */
enum class TransactionCategory {
    ALIMENTATION, TRANSPORT, LOISIRS, ABONNEMENTS, LOGEMENT, SANTE, AUTRE
}

/**
 * Schema uses plain UUID primary keys (not Exposed's id-tables) so every read/write
 * stays in the raw-UUID DSL — simpler and no EntityID wrapping to reason about.
 * Money is always DECIMAL(12,2); never Double — a bank jury will check this first.
 */

object Users : Table("users") {
    val id = javaUUID("id")
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}

object Accounts : Table("accounts") {
    val id = javaUUID("id")
    val userId = reference("user_id", Users.id)
    val label = varchar("label", 120)
    val ibanMasked = varchar("iban_masked", 34)
    val balance = decimal("balance", 12, 2)
    val currency = varchar("currency", 3)
    override val primaryKey = PrimaryKey(id)
}

object Transactions : Table("transactions") {
    val id = javaUUID("id")
    val accountId = reference("account_id", Accounts.id)
    val date = date("date")
    val label = varchar("label", 200)
    val amount = decimal("amount", 12, 2)
    val category = enumerationByName<TransactionCategory>("category", 20)
    override val primaryKey = PrimaryKey(id)
}

object SavingsGoals : Table("savings_goals") {
    val id = javaUUID("id")
    val userId = reference("user_id", Users.id)
    val label = varchar("label", 120)
    val targetAmount = decimal("target_amount", 12, 2)
    val currentAmount = decimal("current_amount", 12, 2)
    override val primaryKey = PrimaryKey(id)
}

object Transfers : Table("transfers") {
    val id = javaUUID("id")
    val fromAccountId = reference("from_account_id", Accounts.id)
    val goalId = reference("goal_id", SavingsGoals.id)
    val amount = decimal("amount", 12, 2)
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}

/** Every table in creation order (parents before children, for FK integrity). */
val allTables = arrayOf(Users, Accounts, Transactions, SavingsGoals, Transfers)
