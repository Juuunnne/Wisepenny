package com.wisepenny.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisepenny.domain.model.Challenge
import com.wisepenny.domain.model.Goal
import com.wisepenny.domain.repository.ChallengeRepository
import com.wisepenny.domain.repository.ContributionRepository
import com.wisepenny.domain.repository.GoalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class DashboardViewModel(
    goalRepository: GoalRepository,
    challengeRepository: ChallengeRepository,
    contributionRepository: ContributionRepository,
) : ViewModel() {

    private val thisMonthStart: LocalDate = today().let { LocalDate(it.year, it.month, 1) }
    private val nextMonthStart: LocalDate = thisMonthStart.plus(1, DateTimeUnit.MONTH)
    private val lastMonthStart: LocalDate = thisMonthStart.minus(1, DateTimeUnit.MONTH)

    val uiState: StateFlow<DashboardUiState> = combine(
        goalRepository.observeAll(),
        challengeRepository.observeActiveChallenges(),
        contributionRepository.observeSumBetween(thisMonthStart, nextMonthStart),
        contributionRepository.observeSumBetween(lastMonthStart, thisMonthStart),
    ) { goals, challenges, savedThisMonth, savedLastMonth ->
        buildState(goals, challenges, savedThisMonth, savedLastMonth)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EmptyState,
    )

    private fun buildState(
        goals: List<Goal>,
        challenges: List<Challenge>,
        savedThisMonth: Long,
        savedLastMonth: Long,
    ): DashboardUiState {
        val delta = if (savedLastMonth > 0) {
            val pct = (savedThisMonth - savedLastMonth) * 100 / savedLastMonth
            "${if (pct >= 0) "+" else ""}$pct% vs mois dernier"
        } else {
            null
        }
        return DashboardUiState(
            greetingName = GREETING_NAME,
            avatarInitials = AVATAR_INITIALS,
            savedThisMonthLabel = formatEuros(savedThisMonth),
            monthlyDeltaLabel = delta,
            monthlyObjectiveLabel = "Objectif mensuel : ${formatEuros(MONTHLY_OBJECTIVE_CENTS)}",
            monthlyProgress = (savedThisMonth.toFloat() / MONTHLY_OBJECTIVE_CENTS).coerceIn(0f, 1f),
            streakDays = STREAK_DAYS,
            streakTotal = STREAK_TOTAL,
            goals = goals.map { it.toDashboardGoal() },
            activeChallenges = challenges.map { it.toDashboardChallenge() },
        )
    }

    private fun Goal.toDashboardGoal(): DashboardGoalItem {
        val ratio = if (targetAmountCents > 0) {
            (savedAmountCents.toFloat() / targetAmountCents).coerceIn(0f, 1f)
        } else {
            0f
        }
        return DashboardGoalItem(
            id = id,
            iconKey = iconKey,
            name = name,
            savedLabel = formatEuros(savedAmountCents),
            targetLabel = formatEuros(targetAmountCents),
            progress = ratio,
            isPriority = isPriority,
        )
    }

    private fun Challenge.toDashboardChallenge(): DashboardChallengeItem {
        val pct = if (totalDays > 0) completedDays * 100 / totalDays else 0
        return DashboardChallengeItem(
            id = id,
            iconKey = iconKeyForChallenge(title),
            title = title,
            dayLabel = "Jour ${completedDays.coerceAtMost(totalDays)} sur $totalDays",
            percentLabel = "$pct%",
        )
    }

    private fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    companion object {
        // Placeholders until onboarding/profile and the global-streak story land.
        private const val GREETING_NAME = "Léa"
        private const val AVATAR_INITIALS = "LM"
        private const val MONTHLY_OBJECTIVE_CENTS = 200_00L
        private const val STREAK_DAYS = 5
        private const val STREAK_TOTAL = 7

        private val EmptyState = DashboardUiState(
            greetingName = GREETING_NAME,
            avatarInitials = AVATAR_INITIALS,
            savedThisMonthLabel = "0 €",
            monthlyDeltaLabel = null,
            monthlyObjectiveLabel = "Objectif mensuel : 200 €",
            monthlyProgress = 0f,
            streakDays = STREAK_DAYS,
            streakTotal = STREAK_TOTAL,
            goals = emptyList(),
            activeChallenges = emptyList(),
        )

        private fun iconKeyForChallenge(title: String): String = when {
            title.contains("café", ignoreCase = true) -> "coffee"
            title.contains("achat", ignoreCase = true) -> "shopping"
            else -> "gift"
        }

        private fun formatEuros(cents: Long): String {
            val euros = cents / 100
            val grouped = euros.toString()
                .reversed()
                .chunked(3)
                .joinToString(" ")
                .reversed()
            return "$grouped €"
        }
    }
}
