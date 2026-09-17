package com.wisepenny.presentation.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisepenny.domain.model.Challenge
import com.wisepenny.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LinkChallengeViewModel(
    private val challengeRepository: ChallengeRepository,
) : ViewModel() {

    private val goalId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<LinkChallengeUiState> = challengeRepository.observeUnlinked()
        .map { challenges -> LinkChallengeUiState(challenges.map { it.toItem() }) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LinkChallengeUiState(),
        )

    fun selectGoal(id: Long) {
        goalId.value = id
    }

    fun onLinkChallenge(challengeId: Long) {
        val id = goalId.value ?: return
        viewModelScope.launch {
            challengeRepository.linkToGoal(challengeId, id)
        }
    }

    private fun Challenge.toItem(): LinkableChallengeItem {
        val euros = dailyAmountCents / 100
        return LinkableChallengeItem(
            id = id,
            iconKey = iconKeyForChallenge(title),
            title = title,
            subtitle = subtitle,
            amountLabel = "+$euros € / jour",
        )
    }

    companion object {
        private fun iconKeyForChallenge(title: String): String = when {
            title.contains("café", ignoreCase = true) -> "coffee"
            title.contains("achat", ignoreCase = true) -> "shopping"
            else -> "gift"
        }
    }
}
