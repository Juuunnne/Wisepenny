package com.wisepenny.presentation.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisepenny.domain.model.Challenge
import com.wisepenny.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ChallengeViewModel(
    private val repository: ChallengeRepository,
) : ViewModel() {

    private val activeChallenge: StateFlow<Challenge?> = repository.observeActive()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val uiState: StateFlow<ChallengeUiState> = activeChallenge
        .map { challenge -> challenge?.toUiState() ?: EmptyState }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EmptyState,
        )

    fun onValidateDay() {
        val challenge = activeChallenge.value ?: return
        viewModelScope.launch {
            repository.completeToday(challenge.id, today())
        }
    }

    fun onSkipDay() {
        // v1: skipping is silent — no DB write, no streak penalty.
        // Step 9 will decide whether to record skips for analytics.
    }

    private fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun Challenge.toUiState(): ChallengeUiState {
        val euros = dailyAmountCents / 100
        return ChallengeUiState(
            title = title,
            subtitle = subtitle,
            totalSavings = "${completedDays * euros} €",
            xpGained = "+${completedDays * 5} XP",
            completedDays = completedDays,
            currentDay = (completedDays + 1).coerceAtMost(totalDays),
            totalDays = totalDays,
            todayQuestion = "As-tu sauté ton café ce matin?",
            todayActionLabel = "Oui, $euros € épargnés",
            history = DayLabels.take(completedDays).map { HistoryDay(it, "+$euros €") },
        )
    }

    companion object {
        private val DayLabels = listOf(
            "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche",
        )

        private val EmptyState = ChallengeUiState(
            title = "Aucun défi en cours",
            subtitle = "",
            totalSavings = "0 €",
            xpGained = "0 XP",
            completedDays = 0,
            currentDay = 0,
            totalDays = 1,
            todayQuestion = "",
            todayActionLabel = "",
            history = emptyList(),
        )
    }
}
