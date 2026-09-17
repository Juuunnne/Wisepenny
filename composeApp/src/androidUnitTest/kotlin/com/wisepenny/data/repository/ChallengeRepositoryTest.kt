package com.wisepenny.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.wisepenny.data.mapper.toDomain
import com.wisepenny.db.WisepennyDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Regression test: ChallengeDetailRoute navigation used to always show the first-inserted
 * challenge regardless of the requested id, because the screen queried the repository's
 * single "active challenge" row instead of looking up by id.
 */
class ChallengeRepositoryTest {

    private lateinit var repository: ChallengeRepositoryImpl
    private lateinit var database: WisepennyDatabase

    @BeforeTest
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        WisepennyDatabase.Schema.create(driver)
        database = WisepennyDatabase(driver)
        repository = ChallengeRepositoryImpl(database)
    }

    @Test
    fun observeByIdReturnsTheMatchingChallengeNotJustTheFirstOne() = runBlocking {
        repository.create(
            title = "7 jours sans café",
            subtitle = "",
            dailyAmountCents = 300L,
            totalDays = 7,
            startDate = LocalDate(2026, 1, 1),
        )
        repository.create(
            title = "Pas d'achats impulsifs",
            subtitle = "",
            dailyAmountCents = 500L,
            totalDays = 7,
            startDate = LocalDate(2026, 1, 1),
        )
        val challenges = database.challengeQueries.selectUnlinked().executeAsList().map { it.toDomain() }
        val secondChallengeId = challenges[1].id

        val result = repository.observeById(secondChallengeId).first()

        assertEquals(
            "Pas d'achats impulsifs",
            result?.title,
            "observeById(secondChallengeId) must return the second challenge, not the first",
        )
    }
}
