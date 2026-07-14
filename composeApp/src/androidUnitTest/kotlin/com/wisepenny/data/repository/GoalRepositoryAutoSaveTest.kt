package com.wisepenny.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.wisepenny.data.mapper.toDomain
import com.wisepenny.db.WisepennyDatabase
import com.wisepenny.domain.model.SavingsCadence
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Exercises the auto-save money math in [GoalRepositoryImpl.applyDueAutoSaves] against a real
 * in-memory SQLite database. This is the only real business calculation in the app, so it is the
 * highest-value thing to protect: a bug here means the user's saved balance is wrong.
 */
class GoalRepositoryAutoSaveTest {

    private lateinit var repository: GoalRepositoryImpl
    private lateinit var database: WisepennyDatabase

    @BeforeTest
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        WisepennyDatabase.Schema.create(driver)
        database = WisepennyDatabase(driver)
        repository = GoalRepositoryImpl(database)
    }

    private fun savedAmount(goalId: Long): Long =
        database.goalQueries.selectById(goalId).executeAsOne().toDomain().savedAmountCents

    private fun createGoalWithAutoSave(
        targetAmountCents: Long,
        autoSaveCents: Long,
        cadence: SavingsCadence,
        lastApplied: LocalDate,
    ): Long = runBlocking {
        val id = repository.create(
            name = "Test",
            category = "Épargne",
            subtitle = "",
            iconKey = "icon",
            targetAmountCents = targetAmountCents,
            isPriority = false,
            targetDate = null,
            createdDate = lastApplied,
        )
        // setAutoSave stores `today` as the last-applied date, so we pass a past date here.
        repository.setAutoSave(id, autoSaveCents, cadence, lastApplied)
        id
    }

    @Test
    fun appliesMultiplePeriodsAtOnce() = runBlocking {
        // Weekly auto-save of 10€, last applied 21 days ago -> 3 full weeks due.
        val id = createGoalWithAutoSave(
            targetAmountCents = 100_00L,
            autoSaveCents = 10_00L,
            cadence = SavingsCadence.WEEKLY,
            lastApplied = LocalDate(2026, 1, 1),
        )

        repository.applyDueAutoSaves(today = LocalDate(2026, 1, 22))

        assertEquals(30_00L, savedAmount(id), "3 weeks x 10€ should be saved")
    }

    @Test
    fun capsContributionAtRemainingTarget() = runBlocking {
        // 3 weeks due (30€) but only 25€ left to reach the target -> cap at 25€.
        val id = createGoalWithAutoSave(
            targetAmountCents = 25_00L,
            autoSaveCents = 10_00L,
            cadence = SavingsCadence.WEEKLY,
            lastApplied = LocalDate(2026, 1, 1),
        )

        repository.applyDueAutoSaves(today = LocalDate(2026, 1, 22))

        assertEquals(25_00L, savedAmount(id), "should never save past the target")
    }

    @Test
    fun doesNothingWhenNoFullPeriodHasElapsed() = runBlocking {
        // Only 3 days since last applied, less than a week -> no contribution.
        val id = createGoalWithAutoSave(
            targetAmountCents = 100_00L,
            autoSaveCents = 10_00L,
            cadence = SavingsCadence.WEEKLY,
            lastApplied = LocalDate(2026, 1, 1),
        )

        repository.applyDueAutoSaves(today = LocalDate(2026, 1, 4))

        assertEquals(0L, savedAmount(id), "no full week elapsed, nothing should be saved")
    }
}
