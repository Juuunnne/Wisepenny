package com.wisepenny.presentation.dashboard

import com.wisepenny.presentation.FakeChallengeRepository
import com.wisepenny.presentation.FakeContributionRepository
import com.wisepenny.presentation.FakeGoalRepository
import com.wisepenny.presentation.FakeProfileRepository
import com.wisepenny.presentation.aChallenge
import com.wisepenny.presentation.aGoal
import com.wisepenny.presentation.aProfile
import com.wisepenny.presentation.subscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    // The VM derives its month windows from the real system clock, so the fake
    // contribution repo must classify windows against that same "today".
    private val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel(
        goals: List<com.wisepenny.domain.model.Goal> = emptyList(),
        challenges: List<com.wisepenny.domain.model.Challenge> = emptyList(),
        profile: com.wisepenny.domain.model.Profile? = null,
        thisMonthCents: Long = 0L,
        lastMonthCents: Long = 0L,
    ): DashboardViewModel = DashboardViewModel(
        goalRepository = FakeGoalRepository(goals),
        challengeRepository = FakeChallengeRepository(challenges),
        contributionRepository = FakeContributionRepository(thisMonthCents, lastMonthCents, today),
        profileRepository = FakeProfileRepository(profile),
    )

    @Test
    fun uiState_isEmpty_whenNothingSaved() = runTest(dispatcher) {
        val vm = buildViewModel()
        subscribe(vm.uiState)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals("", state.greetingName)
        assertEquals("0 €", state.savedThisMonthLabel)
        assertNull(state.monthlyDeltaLabel)
        assertEquals(0f, state.monthlyProgress)
        assertTrue(state.goals.isEmpty())
        assertTrue(state.activeChallenges.isEmpty())
    }

    @Test
    fun greetingAndInitials_comeFromProfileFirstName() = runTest(dispatcher) {
        val vm = buildViewModel(profile = aProfile(firstName = "Junhao Lee"))
        subscribe(vm.uiState)
        advanceUntilIdle()

        assertEquals("Junhao Lee", vm.uiState.value.greetingName)
        assertEquals("JL", vm.uiState.value.avatarInitials)
    }

    @Test
    fun monthlyDelta_isPositivePercentage_whenSavingMoreThanLastMonth() = runTest(dispatcher) {
        val vm = buildViewModel(thisMonthCents = 150_00L, lastMonthCents = 100_00L)
        subscribe(vm.uiState)
        advanceUntilIdle()

        assertEquals("150 €", vm.uiState.value.savedThisMonthLabel)
        assertEquals("+50% vs mois dernier", vm.uiState.value.monthlyDeltaLabel)
        assertEquals(0.75f, vm.uiState.value.monthlyProgress) // 150 / 200 objective
    }

    @Test
    fun monthlyDelta_isNegativePercentage_whenSavingLessThanLastMonth() = runTest(dispatcher) {
        val vm = buildViewModel(thisMonthCents = 50_00L, lastMonthCents = 100_00L)
        subscribe(vm.uiState)
        advanceUntilIdle()

        assertEquals("-50% vs mois dernier", vm.uiState.value.monthlyDeltaLabel)
    }

    @Test
    fun monthlyProgress_isCappedAtOne_whenObjectiveExceeded() = runTest(dispatcher) {
        val vm = buildViewModel(thisMonthCents = 300_00L) // objective is 200 €
        subscribe(vm.uiState)
        advanceUntilIdle()

        assertEquals(1f, vm.uiState.value.monthlyProgress)
    }

    @Test
    fun goals_areMappedWithProgressAndLabels() = runTest(dispatcher) {
        val vm = buildViewModel(
            goals = listOf(
                aGoal(id = 1, name = "Japon", targetAmountCents = 100_00L, savedAmountCents = 50_00L, isPriority = true),
                aGoal(id = 2, name = "Ordi", targetAmountCents = 0L, savedAmountCents = 10_00L),
            ),
        )
        subscribe(vm.uiState)
        advanceUntilIdle()

        val japon = vm.uiState.value.goals.first { it.id == 1L }
        assertEquals("50 €", japon.savedLabel)
        assertEquals("100 €", japon.targetLabel)
        assertEquals(0.5f, japon.progress)
        assertTrue(japon.isPriority)

        // A zero target must not divide-by-zero; progress stays 0.
        assertEquals(0f, vm.uiState.value.goals.first { it.id == 2L }.progress)
    }

    @Test
    fun challenges_areMappedWithPercentAndCoercedDayLabel() = runTest(dispatcher) {
        val vm = buildViewModel(
            challenges = listOf(aChallenge(id = 9, title = "7 jours sans café", totalDays = 7, completedDays = 2)),
        )
        subscribe(vm.uiState)
        advanceUntilIdle()

        val item = vm.uiState.value.activeChallenges.single()
        assertEquals("coffee", item.iconKey)
        assertEquals("Jour 2 sur 7", item.dayLabel)
        assertEquals("28%", item.percentLabel) // 2 * 100 / 7
    }
}
