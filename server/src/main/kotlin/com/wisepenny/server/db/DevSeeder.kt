package com.wisepenny.server.db

import at.favre.lib.crypto.bcrypt.BCrypt
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

/**
 * Fills an empty database with believable demo data for a French 18–26 user:
 * one login, two accounts, ~200 categorised transactions over the last 6 months,
 * and two savings goals. Runs only in the `dev` environment and only when the
 * `users` table is empty, so restarts don't duplicate rows.
 *
 * Demo login: demo@wisepenny.fr / demo1234
 */
object DevSeeder {

    private val log = LoggerFactory.getLogger(DevSeeder::class.java)

    private data class Merchant(
        val label: String,
        val category: TransactionCategory,
        val min: Int,
        val max: Int,
    )

    private val expenses = listOf(
        Merchant("Lidl", TransactionCategory.ALIMENTATION, 8, 55),
        Merchant("Carrefour City", TransactionCategory.ALIMENTATION, 5, 40),
        Merchant("Franprix", TransactionCategory.ALIMENTATION, 6, 45),
        Merchant("Uber Eats", TransactionCategory.ALIMENTATION, 12, 35),
        Merchant("McDonald's", TransactionCategory.ALIMENTATION, 7, 22),
        Merchant("Abonnement TCL", TransactionCategory.TRANSPORT, 35, 35),
        Merchant("Uber", TransactionCategory.TRANSPORT, 6, 28),
        Merchant("SNCF Connect", TransactionCategory.TRANSPORT, 19, 89),
        Merchant("TotalEnergies", TransactionCategory.TRANSPORT, 30, 70),
        Merchant("Spotify", TransactionCategory.ABONNEMENTS, 11, 11),
        Merchant("Netflix", TransactionCategory.ABONNEMENTS, 14, 14),
        Merchant("Free Mobile", TransactionCategory.ABONNEMENTS, 20, 20),
        Merchant("Amazon Prime", TransactionCategory.ABONNEMENTS, 7, 7),
        Merchant("Cinéma Pathé", TransactionCategory.LOISIRS, 8, 15),
        Merchant("Fnac", TransactionCategory.LOISIRS, 15, 90),
        Merchant("Decathlon", TransactionCategory.LOISIRS, 12, 75),
        Merchant("Steam", TransactionCategory.LOISIRS, 5, 60),
        Merchant("Loyer", TransactionCategory.LOGEMENT, 480, 480),
        Merchant("EDF", TransactionCategory.LOGEMENT, 35, 65),
        Merchant("Pharmacie", TransactionCategory.SANTE, 6, 40),
        Merchant("Consultation médecin", TransactionCategory.SANTE, 25, 25),
        Merchant("Retrait DAB", TransactionCategory.AUTRE, 20, 60),
    )

    private val incomes = listOf(
        Merchant("Salaire alternance", TransactionCategory.AUTRE, 900, 1200),
        Merchant("Virement CAF (APL)", TransactionCategory.AUTRE, 180, 200),
        Merchant("Remboursement ami", TransactionCategory.AUTRE, 10, 40),
    )

    fun seed(database: Database) = transaction(database) {
        if (Users.selectAll().count() > 0) {
            log.info("Seed skipped — users table already populated.")
            return@transaction
        }

        val rng = Random(42) // fixed seed → reproducible demo dataset
        val today = LocalDate.now()

        val userId = UUID.randomUUID()
        Users.insert {
            it[id] = userId
            it[email] = "demo@wisepenny.fr"
            it[passwordHash] = BCrypt.withDefaults().hashToString(12, "demo1234".toCharArray())
            it[createdAt] = LocalDateTime.now()
        }

        val checkingId = UUID.randomUUID()
        val savingsId = UUID.randomUUID()
        Accounts.insert {
            it[id] = checkingId
            it[Accounts.userId] = userId
            it[label] = "Compte courant"
            it[ibanMasked] = "FR76 •••• •••• •••• 4821"
            it[balance] = BigDecimal("1250.75")
            it[currency] = "EUR"
        }
        Accounts.insert {
            it[id] = savingsId
            it[Accounts.userId] = userId
            it[label] = "Livret A"
            it[ibanMasked] = "FR76 •••• •••• •••• 9013"
            it[balance] = BigDecimal("320.40")
            it[currency] = "EUR"
        }
        val accountIds = listOf(checkingId, savingsId)

        var count = 0
        repeat(200) {
            val isIncome = rng.nextInt(12) == 0
            val m = if (isIncome) incomes.random(rng) else expenses.random(rng)
            val whole = if (m.min == m.max) m.min else rng.nextInt(m.min, m.max + 1)
            val cents = rng.nextInt(0, 100).toString().padStart(2, '0')
            val magnitude = BigDecimal("$whole.$cents")

            Transactions.insert {
                it[id] = UUID.randomUUID()
                it[accountId] = accountIds.random(rng)
                it[date] = today.minusDays(rng.nextInt(0, 181).toLong())
                it[label] = m.label
                it[amount] = if (isIncome) magnitude else magnitude.negate()
                it[category] = m.category
            }
            count++
        }

        SavingsGoals.insert {
            it[id] = UUID.randomUUID()
            it[SavingsGoals.userId] = userId
            it[label] = "Voyage au Japon"
            it[targetAmount] = BigDecimal("3000.00")
            it[currentAmount] = BigDecimal("450.00")
        }
        SavingsGoals.insert {
            it[id] = UUID.randomUUID()
            it[SavingsGoals.userId] = userId
            it[label] = "MacBook Air"
            it[targetAmount] = BigDecimal("1600.00")
            it[currentAmount] = BigDecimal("200.00")
        }

        log.info("Seed complete — 1 user, 2 accounts, $count transactions, 2 goals.")
    }
}
