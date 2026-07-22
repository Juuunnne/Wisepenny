package com.wisepenny.data.mapper

import com.wisepenny.domain.model.SavingsCadence
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import com.wisepenny.db.Goal as GoalRow
import com.wisepenny.db.Profile as ProfileRow

class MapperTest {

    @Test
    fun goalRow_mapsAllFields_whenAutoSaveIsSet() {
        val row = GoalRow(
            id = 1L,
            name = "Vacances",
            category = "Voyage",
            subtitle = "Été 2026",
            iconKey = "beach",
            targetAmountCents = 100_000L,
            savedAmountCents = 25_000L,
            isPriority = 1L,
            targetDate = "2026-08-01",
            autoSaveAmountCents = 5_000L,
            autoSaveCadence = "WEEKLY",
            createdDate = "2026-01-15",
            autoSaveLastAppliedDate = "2026-06-01",
        )

        val goal = row.toDomain()

        assertEquals("Vacances", goal.name)
        assertEquals(100_000L, goal.targetAmountCents)
        assertEquals(25_000L, goal.savedAmountCents)
        assertTrue(goal.isPriority, "isPriority == 1L should map to true")
        assertEquals(LocalDate(2026, 8, 1), goal.targetDate)
        assertEquals(5_000L, goal.autoSaveAmountCents)
        assertEquals(SavingsCadence.WEEKLY, goal.autoSaveCadence)
        assertEquals(LocalDate(2026, 1, 15), goal.createdDate)
        assertEquals(LocalDate(2026, 6, 1), goal.autoSaveLastAppliedDate)
    }

    @Test
    fun goalRow_mapsNullables_whenAutoSaveIsAbsent() {
        val row = GoalRow(
            id = 2L,
            name = "Fonds d'urgence",
            category = "Épargne",
            subtitle = "",
            iconKey = "shield",
            targetAmountCents = 300_000L,
            savedAmountCents = 0L,
            isPriority = 0L,
            targetDate = null,
            autoSaveAmountCents = null,
            autoSaveCadence = null,
            createdDate = "2026-02-20",
            autoSaveLastAppliedDate = null,
        )

        val goal = row.toDomain()

        assertEquals(false, goal.isPriority, "isPriority == 0L should map to false")
        assertNull(goal.targetDate)
        assertNull(goal.autoSaveAmountCents)
        assertNull(goal.autoSaveCadence)
        assertNull(goal.autoSaveLastAppliedDate)
    }

    @Test
    fun profileRow_mapsBooleanFlagsFromIntegers() {
        val row = ProfileRow(
            id = 1L,
            firstName = "Alex",
            motivation = "Économiser pour un voyage",
            createdDate = "2026-03-10",
            onboardingCompleted = 1L,
            currency = "EUR",
            notificationsOptIn = 0L,
            bankLinked = 1L,
        )

        val profile = row.toDomain()

        assertEquals("Économiser pour un voyage", profile.motivation)
        assertEquals(LocalDate(2026, 3, 10), profile.createdDate)
        assertTrue(profile.onboardingCompleted)
        assertEquals(false, profile.notificationsOptIn)
        assertTrue(profile.bankLinked)
        assertEquals("EUR", profile.currency)
    }
}
