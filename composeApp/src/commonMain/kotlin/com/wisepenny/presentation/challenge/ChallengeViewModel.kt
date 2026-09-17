package com.wisepenny.presentation.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisepenny.domain.model.Challenge
import com.wisepenny.domain.repository.ChallengeRepository
import com.wisepenny.presentation.goal.goalEmoji
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ChallengeViewModel(
    private val repository: ChallengeRepository,
) : ViewModel() {

    private val selectedId = MutableStateFlow<Long?>(null)
    private val hasAnsweredToday = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val selectedChallenge: StateFlow<Challenge?> = selectedId
        .flatMapLatest { id -> if (id == null) flowOf(null) else repository.observeById(id) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val uiState: StateFlow<ChallengeUiState> = combine(
        selectedChallenge,
        hasAnsweredToday,
    ) { challenge, answered -> challenge?.toUiState(answered) ?: EmptyState }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EmptyState,
        )

    fun selectChallenge(id: Long) {
        if (selectedId.value != id) hasAnsweredToday.value = false
        selectedId.value = id
    }

    fun onValidateDay() {
        val challenge = selectedChallenge.value ?: return
        hasAnsweredToday.value = true
        viewModelScope.launch {
            repository.completeToday(challenge.id, today())
        }
    }

    fun onSkipDay() {
        // v1: skipping is silent — no DB write, no streak penalty.
        // Step 9 will decide whether to record skips for analytics.
        hasAnsweredToday.value = true
    }

    private fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun Challenge.toUiState(hasAnsweredToday: Boolean): ChallengeUiState {
        val euros = dailyAmountCents / 100
        return ChallengeUiState(
            title = title,
            subtitle = subtitle,
            totalSavings = "${completedDays * euros} €",
            xpGained = "+${completedDays * 5} XP",
            completedDays = completedDays,
            currentDay = (completedDays + 1).coerceAtMost(totalDays),
            totalDays = totalDays,
            todayEmoji = goalEmoji(iconKeyForChallenge(title)),
            todayQuestion = "As-tu respecté ton défi aujourd'hui ?",
            todayActionLabel = "Oui, $euros € épargnés",
            hasAnsweredToday = hasAnsweredToday,
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
            todayEmoji = "🎁",
            todayQuestion = "",
            todayActionLabel = "",
            hasAnsweredToday = true,
            history = emptyList(),
        )

        private fun iconKeyForChallenge(title: String): String = when {
            title.contains("café", ignoreCase = true) -> "coffee"
            title.contains("achat", ignoreCase = true) -> "shopping"
            else -> "gift"
        }
    }
}
