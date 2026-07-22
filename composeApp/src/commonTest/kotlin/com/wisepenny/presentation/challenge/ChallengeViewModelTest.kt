package com.wisepenny.presentation.challenge

import com.wisepenny.presentation.FakeChallengeRepository
import com.wisepenny.presentation.aChallenge
import com.wisepenny.presentation.subscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ChallengeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun uiState_isEmptyState_whenNoActiveChallenge() = runTest(dispatcher) {
        val vm = ChallengeViewModel(FakeChallengeRepository())
        subscribe(vm.uiState)
        advanceUntilIdle()

        assertEquals("Aucun défi en cours", vm.uiState.value.title)
        assertEquals("0 €", vm.uiState.value.totalSavings)
        assertTrue(vm.uiState.value.history.isEmpty())
    }

    @Test
    fun uiState_mapsActiveChallenge_toSavingsXpAndHistory() = runTest(dispatcher) {
        // 3 €/day, 2 days done -> 6 € saved, +10 XP, two history rows.
        val repo = FakeChallengeRepository(
            listOf(aChallenge(id = 7, dailyAmountCents = 300, totalDays = 7, completedDays = 2)),
        )
        val vm = ChallengeViewModel(repo)
        subscribe(vm.uiState)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals("6 €", state.totalSavings)
        assertEquals("+10 XP", state.xpGained)
        assertEquals(2, state.completedDays)
        assertEquals(3, state.currentDay) // completedDays + 1
        assertEquals(listOf("Lundi", "Mardi"), state.history.map(HistoryDay::dayLabel))
        assertEquals("Oui, 3 € épargnés", state.todayActionLabel)
    }

    @Test
    fun uiState_capsCurrentDay_atTotalDays_whenAllDaysDone() = runTest(dispatcher) {
        val repo = FakeChallengeRepository(
            listOf(aChallenge(dailyAmountCents = 300, totalDays = 7, completedDays = 7)),
        )
        val vm = ChallengeViewModel(repo)
        subscribe(vm.uiState)
        advanceUntilIdle()

        assertEquals(7, vm.uiState.value.currentDay) // (7 + 1) coerced to totalDays
    }

    @Test
    fun onValidateDay_completesToday_forActiveChallenge() = runTest(dispatcher) {
        val repo = FakeChallengeRepository(listOf(aChallenge(id = 42)))
        val vm = ChallengeViewModel(repo)
        subscribe(vm.uiState)
        advanceUntilIdle()

        vm.onValidateDay()
        advanceUntilIdle()

        assertEquals(1, repo.completions.size)
        assertEquals(42L, repo.completions.first().challengeId)
    }

    @Test
    fun onValidateDay_doesNothing_whenNoActiveChallenge() = runTest(dispatcher) {
        val repo = FakeChallengeRepository()
        val vm = ChallengeViewModel(repo)
        subscribe(vm.uiState)
        advanceUntilIdle()

        vm.onValidateDay()
        advanceUntilIdle()

        assertTrue(repo.completions.isEmpty())
    }

    @Test
    fun onSkipDay_isSilentNoOp() = runTest(dispatcher) {
        val repo = FakeChallengeRepository(listOf(aChallenge(id = 42)))
        val vm = ChallengeViewModel(repo)
        subscribe(vm.uiState)
        advanceUntilIdle()

        vm.onSkipDay()
        advanceUntilIdle()

        assertTrue(repo.completions.isEmpty(), "skipping must not write to the DB in v1")
    }
}
