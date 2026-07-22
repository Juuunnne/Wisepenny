package com.wisepenny.presentation.onboarding

import com.wisepenny.data.seed.DataSeeder
import com.wisepenny.domain.model.SavingsCadence
import com.wisepenny.presentation.FakeChallengeRepository
import com.wisepenny.presentation.FakeGoalRepository
import com.wisepenny.presentation.FakeModuleProgressRepository
import com.wisepenny.presentation.FakeProfileRepository
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var profileRepo: FakeProfileRepository
    private lateinit var goalRepo: FakeGoalRepository
    private lateinit var challengeRepo: FakeChallengeRepository
    private lateinit var moduleProgressRepo: FakeModuleProgressRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        profileRepo = FakeProfileRepository()
        goalRepo = FakeGoalRepository()
        challengeRepo = FakeChallengeRepository()
        moduleProgressRepo = FakeModuleProgressRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel() = OnboardingViewModel(
        profileRepository = profileRepo,
        goalRepository = goalRepo,
        dataSeeder = DataSeeder(goalRepo, challengeRepo, moduleProgressRepo),
    )

    @Test
    fun initialState_startsOnWelcomeStepInEuros() {
        val vm = buildViewModel()
        val state = vm.uiState.value

        assertEquals(OnboardingStep.WELCOME, state.step)
        assertEquals("EUR", state.currency)
        assertTrue(state.isFirstStep)
        assertFalse(state.isLastStep)
        assertTrue(state.canProceed) // WELCOME has no gate
    }

    @Test
    fun onAmountChange_keepsDigitsOnly_andComputesCents() {
        val vm = buildViewModel()
        vm.onAmountChange("1 200 € abc")

        assertEquals("1200", vm.uiState.value.amountText)
        assertEquals(120_000L, vm.uiState.value.amountCents)
    }

    @Test
    fun monthlyCents_dividesTargetOverHorizon_roundingUp() {
        val vm = buildViewModel()
        vm.onAmountChange("1200")
        vm.onHorizonSelect(12)

        // ceil(120000 / 12) = 10000 cents = 100 €/month
        assertEquals(10_000L, vm.uiState.value.monthlyCents)
    }

    @Test
    fun next_isBlocked_onMotivationStep_untilMotivationChosen() {
        val vm = buildViewModel()
        vm.next() // WELCOME -> CURRENCY
        vm.next() // CURRENCY -> MOTIVATION
        assertEquals(OnboardingStep.MOTIVATION, vm.uiState.value.step)

        vm.next() // blocked: no motivation
        assertEquals(OnboardingStep.MOTIVATION, vm.uiState.value.step)

        vm.onMotivationSelect(OnboardingMotivation.ALL.first())
        vm.next()
        assertEquals(OnboardingStep.GOAL, vm.uiState.value.step)
    }

    @Test
    fun next_isBlocked_onGoalStep_untilNameAndAmountProvided() {
        val vm = buildViewModel()
        vm.onMotivationSelect(OnboardingMotivation.ALL.first())
        repeat(3) { vm.next() } // -> GOAL
        assertEquals(OnboardingStep.GOAL, vm.uiState.value.step)

        vm.next() // blocked: no name/amount
        assertEquals(OnboardingStep.GOAL, vm.uiState.value.step)

        vm.onNameChange("Japon")
        vm.onAmountChange("1000")
        vm.next()
        assertEquals(OnboardingStep.PREVIEW, vm.uiState.value.step)
    }

    @Test
    fun back_goesToPreviousStep_andIsNoOpOnWelcome() {
        val vm = buildViewModel()
        vm.next() // -> CURRENCY
        vm.back() // -> WELCOME
        assertEquals(OnboardingStep.WELCOME, vm.uiState.value.step)

        vm.back() // no-op on first step
        assertEquals(OnboardingStep.WELCOME, vm.uiState.value.step)
    }

    @Test
    fun nextOnNotifications_optsIn_skipOptsOut() {
        val optIn = buildViewModel().apply { navigateToNotifications() }
        optIn.next()
        assertEquals(OnboardingStep.BANK, optIn.uiState.value.step)
        assertTrue(optIn.uiState.value.notificationsOptIn)

        val optOut = buildViewModel().apply { navigateToNotifications() }
        optOut.skip()
        assertEquals(OnboardingStep.BANK, optOut.uiState.value.step)
        assertFalse(optOut.uiState.value.notificationsOptIn)
    }

    @Test
    fun completing_seedsDemoWorld_createsPriorityGoal_andSavesProfile() = runTest(dispatcher) {
        val vm = buildViewModel()
        vm.navigateToNotifications()
        // Override the helper's placeholder values with the real ones before submitting.
        vm.onFirstNameChange("  Junhao  ")
        vm.onNameChange("Voyage au Japon")
        vm.onAmountChange("1200")
        vm.onMotivationSelect(OnboardingMotivation.ALL.first()) // Voyager / travel / Voyage
        vm.next() // NOTIFICATIONS -> BANK (opt in)
        vm.next() // BANK -> complete()
        advanceUntilIdle()

        // Profile written with trimmed name and the collected flags.
        val profile = profileRepo.profile.value
        assertNotNull(profile)
        assertEquals("Junhao", profile.firstName)
        assertEquals("EUR", profile.currency)
        assertTrue(profile.onboardingCompleted)
        assertTrue(profile.notificationsOptIn)
        assertTrue(profile.bankLinked)

        // The user's own goal is the single priority goal, on top of the demo world.
        val priorityGoals = goalRepo.goals.value.filter { it.isPriority }
        assertEquals(1, priorityGoals.size)
        val userGoal = priorityGoals.single()
        assertEquals("Voyage au Japon", userGoal.name)
        assertEquals(120_000L, userGoal.targetAmountCents)
        assertEquals("Voyage", userGoal.category)

        // Deadline turned into a monthly auto-save on that goal.
        val autoSave = goalRepo.autoSaves.single { it.goalId == userGoal.id }
        assertEquals(SavingsCadence.MONTHLY, autoSave.cadence)
        assertEquals(10_000L, autoSave.amountCents)

        // Demo world was actually seeded (more than just the user's goal).
        assertTrue(goalRepo.goals.value.size > 1, "seedDemoWorld should add demo goals")
        assertTrue(vm.uiState.value.isSubmitting)
    }

    @Test
    fun completing_isGuardedAgainstDoubleSubmit() = runTest(dispatcher) {
        val vm = buildViewModel()
        vm.navigateToNotifications()
        vm.next() // -> BANK
        vm.next() // -> complete()
        advanceUntilIdle()

        val goalsAfterFirst = goalRepo.goals.value.size
        vm.next() // BANK.canProceed is false while submitting -> no-op
        advanceUntilIdle()

        assertEquals(goalsAfterFirst, goalRepo.goals.value.size, "must not seed/create a second time")
    }
}

/** Walks the wizard from WELCOME to the NOTIFICATIONS step, filling the required gates. */
private fun OnboardingViewModel.navigateToNotifications() {
    onMotivationSelect(OnboardingMotivation.ALL.first())
    onNameChange("Objectif")
    onAmountChange("1000")
    next() // WELCOME -> CURRENCY
    next() // CURRENCY -> MOTIVATION
    next() // MOTIVATION -> GOAL
    next() // GOAL -> PREVIEW
    next() // PREVIEW -> NOTIFICATIONS
}
