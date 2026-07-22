package com.wisepenny.presentation.goal

import com.wisepenny.domain.model.SavingsCadence
import com.wisepenny.presentation.FakeChallengeRepository
import com.wisepenny.presentation.FakeGoalRepository
import com.wisepenny.presentation.aChallenge
import com.wisepenny.presentation.aGoal
import com.wisepenny.presentation.subscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GoalViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel(
        goalRepo: FakeGoalRepository = FakeGoalRepository(),
        challengeRepo: FakeChallengeRepository = FakeChallengeRepository(),
    ) = GoalViewModel(goalRepo, challengeRepo)

    @Test
    fun listState_mapsGoalsToItems() = runTest(dispatcher) {
        val goalRepo = FakeGoalRepository(
            listOf(aGoal(id = 1, name = "Japon", targetAmountCents = 100_00L, savedAmountCents = 25_00L, isPriority = true)),
        )
        val vm = buildViewModel(goalRepo)
        subscribe(vm.listState)
        advanceUntilIdle()

        val item = vm.listState.value.goals.single()
        assertEquals("Japon", item.name)
        assertEquals("25 €", item.savedLabel)
        assertEquals("/ 100 €", item.targetLabel)
        assertEquals(0.25f, item.progress)
        assertEquals("25% complété", item.percentLabel)
        assertEquals("Il reste 75 €", item.remainingLabel)
        assertTrue(item.isPriority)
    }

    @Test
    fun detailState_isNull_untilAGoalIsSelected() = runTest(dispatcher) {
        val vm = buildViewModel(FakeGoalRepository(listOf(aGoal(id = 1))))
        subscribe(vm.detailState)
        advanceUntilIdle()

        assertNull(vm.detailState.value)
    }

    @Test
    fun selectGoal_populatesDetail_andClearSelectionResetsIt() = runTest(dispatcher) {
        val vm = buildViewModel(FakeGoalRepository(listOf(aGoal(id = 5, name = "Ordi"))))
        subscribe(vm.detailState)
        advanceUntilIdle()

        vm.selectGoal(5)
        advanceUntilIdle()
        assertEquals("Ordi", vm.detailState.value?.name)

        vm.clearSelection()
        advanceUntilIdle()
        assertNull(vm.detailState.value)
    }

    @Test
    fun projection_reportsGoalReached_whenSavedMeetsTarget() = runTest(dispatcher) {
        val vm = buildViewModel(
            FakeGoalRepository(listOf(aGoal(id = 1, targetAmountCents = 100_00L, savedAmountCents = 100_00L))),
        )
        subscribe(vm.detailState)
        vm.selectGoal(1)
        advanceUntilIdle()

        assertEquals("Objectif atteint, bravo 🎉", vm.detailState.value?.projectionMessage)
        assertNull(vm.detailState.value?.projectionDate)
    }

    @Test
    fun projection_usesAutoSaveRate_whenConfigured() = runTest(dispatcher) {
        val vm = buildViewModel(
            FakeGoalRepository(
                listOf(
                    aGoal(
                        id = 1,
                        targetAmountCents = 100_00L,
                        savedAmountCents = 0L,
                        autoSaveAmountCents = 10_00L,
                        autoSaveCadence = SavingsCadence.WEEKLY,
                    ),
                ),
            ),
        )
        subscribe(vm.detailState)
        vm.selectGoal(1)
        advanceUntilIdle()

        assertEquals("Tu atteindras ton objectif le", vm.detailState.value?.projectionMessage)
        assertNotNull(vm.detailState.value?.projectionDate)
    }

    @Test
    fun projection_promptsForAutoSave_whenThereIsNoSavingRate() = runTest(dispatcher) {
        // No auto-save and nothing saved yet -> rate is 0 -> prompt the user.
        val vm = buildViewModel(
            FakeGoalRepository(
                listOf(aGoal(id = 1, targetAmountCents = 100_00L, savedAmountCents = 0L, createdDate = LocalDate(2026, 1, 1))),
            ),
        )
        subscribe(vm.detailState)
        vm.selectGoal(1)
        advanceUntilIdle()

        assertEquals("Définis une auto-épargne pour voir ta projection.", vm.detailState.value?.projectionMessage)
        assertNull(vm.detailState.value?.projectionDate)
    }

    @Test
    fun linkedChallenge_isMapped_withContributionAndStatus() = runTest(dispatcher) {
        val goalRepo = FakeGoalRepository(listOf(aGoal(id = 1, name = "Japon")))
        val challengeRepo = FakeChallengeRepository(
            listOf(aChallenge(id = 2, title = "7 jours sans café", dailyAmountCents = 300, totalDays = 7, completedDays = 3, goalId = 1)),
        )
        val vm = buildViewModel(goalRepo, challengeRepo)
        subscribe(vm.detailState)
        vm.selectGoal(1)
        advanceUntilIdle()

        val linked = vm.detailState.value?.linkedChallenges?.single()
        assertNotNull(linked)
        assertEquals("coffee", linked.iconKey)
        assertEquals("+21 € vers Japon", linked.contributionLabel) // 3 € * 7 days
        assertEquals("Jour 3/7", linked.statusLabel)
        assertTrue(linked.isStarted)
    }

    @Test
    fun onQuickAdd_addsTenEuros_toSelectedGoal() = runTest(dispatcher) {
        val goalRepo = FakeGoalRepository(listOf(aGoal(id = 3)))
        val vm = buildViewModel(goalRepo)
        subscribe(vm.detailState)
        vm.selectGoal(3)
        advanceUntilIdle()

        vm.onQuickAdd()
        advanceUntilIdle()

        val contribution = goalRepo.contributions.single()
        assertEquals(3L, contribution.goalId)
        assertEquals(10_00L, contribution.amountCents)
        assertEquals("quick", contribution.source)
    }

    @Test
    fun onAddManual_addsGivenAmount_toSelectedGoal() = runTest(dispatcher) {
        val goalRepo = FakeGoalRepository(listOf(aGoal(id = 3)))
        val vm = buildViewModel(goalRepo)
        subscribe(vm.detailState)
        vm.selectGoal(3)
        advanceUntilIdle()

        vm.onAddManual(42_00L)
        advanceUntilIdle()

        val contribution = goalRepo.contributions.single()
        assertEquals(42_00L, contribution.amountCents)
        assertEquals("manual", contribution.source)
    }

    @Test
    fun contributionsAndAutoSave_areNoOps_whenNoGoalSelected() = runTest(dispatcher) {
        val goalRepo = FakeGoalRepository(listOf(aGoal(id = 3)))
        val vm = buildViewModel(goalRepo)
        subscribe(vm.detailState)
        advanceUntilIdle()

        vm.onQuickAdd()
        vm.onAddManual(10_00L)
        vm.onSetAutoSave(5_00L, SavingsCadence.WEEKLY)
        advanceUntilIdle()

        assertTrue(goalRepo.contributions.isEmpty())
        assertTrue(goalRepo.autoSaves.isEmpty())
    }

    @Test
    fun onSetAutoSave_configuresSelectedGoal() = runTest(dispatcher) {
        val goalRepo = FakeGoalRepository(listOf(aGoal(id = 3)))
        val vm = buildViewModel(goalRepo)
        subscribe(vm.detailState)
        vm.selectGoal(3)
        advanceUntilIdle()

        vm.onSetAutoSave(5_00L, SavingsCadence.MONTHLY)
        advanceUntilIdle()

        val autoSave = goalRepo.autoSaves.single()
        assertEquals(3L, autoSave.goalId)
        assertEquals(5_00L, autoSave.amountCents)
        assertEquals(SavingsCadence.MONTHLY, autoSave.cadence)
    }
}
