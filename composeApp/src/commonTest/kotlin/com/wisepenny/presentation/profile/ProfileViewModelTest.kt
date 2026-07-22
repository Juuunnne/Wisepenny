package com.wisepenny.presentation.profile

import com.wisepenny.presentation.FakeChallengeRepository
import com.wisepenny.presentation.FakeContributionRepository
import com.wisepenny.presentation.FakeGoalRepository
import com.wisepenny.presentation.FakeModuleProgressRepository
import com.wisepenny.presentation.FakeProfileRepository
import com.wisepenny.presentation.aProfile
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel(
        profileRepo: FakeProfileRepository = FakeProfileRepository(),
        goalRepo: FakeGoalRepository = FakeGoalRepository(),
        contributionRepo: FakeContributionRepository = FakeContributionRepository(today = LocalDate(2026, 7, 22)),
        challengeRepo: FakeChallengeRepository = FakeChallengeRepository(),
        moduleProgressRepo: FakeModuleProgressRepository = FakeModuleProgressRepository(),
    ) = ProfileViewModel(profileRepo, goalRepo, contributionRepo, challengeRepo, moduleProgressRepo)

    @Test
    fun uiState_isDefault_whenNoProfile() = runTest(dispatcher) {
        val vm = buildViewModel()
        subscribe(vm.uiState)
        advanceUntilIdle()

        assertEquals("", vm.uiState.value.motivation)
        assertEquals("", vm.uiState.value.memberSinceLabel)
    }

    @Test
    fun uiState_mapsProfile_toMotivationAndFrenchMemberSinceDate() = runTest(dispatcher) {
        val profileRepo = FakeProfileRepository(
            aProfile(motivation = "Économiser pour un voyage", createdDate = LocalDate(2026, 3, 10)),
        )
        val vm = buildViewModel(profileRepo = profileRepo)
        subscribe(vm.uiState)
        advanceUntilIdle()

        assertEquals("Économiser pour un voyage", vm.uiState.value.motivation)
        assertEquals("10 mars 2026", vm.uiState.value.memberSinceLabel)
    }

    @Test
    fun onResetData_clearsEveryRepository() = runTest(dispatcher) {
        val profileRepo = FakeProfileRepository(aProfile())
        val goalRepo = FakeGoalRepository()
        val contributionRepo = FakeContributionRepository(today = LocalDate(2026, 7, 22))
        val challengeRepo = FakeChallengeRepository()
        val moduleProgressRepo = FakeModuleProgressRepository()
        val vm = buildViewModel(profileRepo, goalRepo, contributionRepo, challengeRepo, moduleProgressRepo)
        subscribe(vm.uiState)
        advanceUntilIdle()

        vm.onResetData()
        advanceUntilIdle()

        assertEquals(1, contributionRepo.clearCount)
        assertEquals(1, goalRepo.clearCount)
        assertEquals(1, challengeRepo.clearCount)
        assertEquals(1, moduleProgressRepo.clearCount)
        assertEquals(1, profileRepo.clearCount)
        // Clearing the profile flips the startup gate back to onboarding.
        assertNull(profileRepo.profile.value)
    }
}
