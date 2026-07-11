package com.wisepenny.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisepenny.domain.model.Profile
import com.wisepenny.domain.repository.ChallengeRepository
import com.wisepenny.domain.repository.ContributionRepository
import com.wisepenny.domain.repository.GoalRepository
import com.wisepenny.domain.repository.ModuleProgressRepository
import com.wisepenny.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

data class ProfileUiState(
    val motivation: String = "",
    val memberSinceLabel: String = "",
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val goalRepository: GoalRepository,
    private val contributionRepository: ContributionRepository,
    private val challengeRepository: ChallengeRepository,
    private val moduleProgressRepository: ModuleProgressRepository,
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = profileRepository.observe()
        .map { profile -> profile?.toUiState() ?: ProfileUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState(),
        )

    /**
     * RGPD droit à l'effacement / demo reset: wipes every table, including the
     * profile. Clearing the profile makes [ProfileRepository.observe] emit null,
     * which returns the startup gate to the onboarding wizard.
     */
    fun onResetData() {
        viewModelScope.launch {
            // Wipe the data tables first; clearing the profile last flips the startup
            // gate to the wizard only once everything else is already empty.
            contributionRepository.clear()
            goalRepository.clear()
            challengeRepository.clear()
            moduleProgressRepository.clear()
            profileRepository.clear()
        }
    }

    private fun Profile.toUiState(): ProfileUiState = ProfileUiState(
        motivation = motivation,
        memberSinceLabel = formatDate(createdDate),
    )

    companion object {
        private val FrenchMonths = listOf(
            "janvier", "février", "mars", "avril", "mai", "juin",
            "juillet", "août", "septembre", "octobre", "novembre", "décembre",
        )

        private fun formatDate(d: LocalDate): String =
            "${d.day} ${FrenchMonths[d.month.ordinal]} ${d.year}"
    }
}
