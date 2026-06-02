package com.wisepenny.presentation.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisepenny.domain.model.Challenge
import com.wisepenny.domain.model.Goal
import com.wisepenny.domain.model.SavingsCadence
import com.wisepenny.domain.repository.ChallengeRepository
import com.wisepenny.domain.repository.GoalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class GoalViewModel(
    private val goalRepository: GoalRepository,
    private val challengeRepository: ChallengeRepository,
) : ViewModel() {

    private val selectedId = MutableStateFlow<Long?>(null)

    val listState: StateFlow<GoalListUiState> = goalRepository.observeAll()
        .map { goals -> GoalListUiState(goals.map { it.toListItem() }) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GoalListUiState(emptyList()),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val detailState: StateFlow<GoalDetailUiState?> = selectedId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(null)
            } else {
                combine(
                    goalRepository.observeById(id),
                    challengeRepository.observeByGoal(id),
                ) { goal, challenges -> goal?.toDetailUiState(challenges) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    init {
        viewModelScope.launch {
            goalRepository.applyDueAutoSaves(today())
            ensureSampleData()
        }
    }

    fun selectGoal(id: Long) {
        selectedId.value = id
    }

    fun clearSelection() {
        selectedId.value = null
    }

    fun onQuickAdd() = addToSelected(QUICK_ADD_CENTS)

    fun onAddManual(amountCents: Long) = addToSelected(amountCents)

    fun onSetAutoSave(amountCents: Long, cadence: SavingsCadence) {
        val id = selectedId.value ?: return
        viewModelScope.launch {
            goalRepository.setAutoSave(id, amountCents, cadence, today())
        }
    }

    private fun addToSelected(amountCents: Long) {
        val id = selectedId.value ?: return
        viewModelScope.launch {
            goalRepository.addContribution(id, amountCents)
        }
    }

    private suspend fun ensureSampleData() {
        if (goalRepository.observeAll().first().isNotEmpty()) return
        val seededOn = today().minus(60, DateTimeUnit.DAY)

        val japonId = goalRepository.create(
            name = "Voyage au Japon",
            category = "Voyage",
            subtitle = "",
            iconKey = "travel",
            targetAmountCents = 3_000_00,
            isPriority = true,
            targetDate = null,
            createdDate = seededOn,
        )
        goalRepository.addContribution(japonId, 1_250_00)

        val ordiId = goalRepository.create(
            name = "Nouvel ordi",
            category = "Tech",
            subtitle = "",
            iconKey = "laptop",
            targetAmountCents = 1_200_00,
            isPriority = false,
            targetDate = null,
            createdDate = seededOn,
        )
        goalRepository.addContribution(ordiId, 450_00)

        val lisbonneId = goalRepository.create(
            name = "Lisbonne 2026",
            category = "Voyage",
            subtitle = "Avec Sarah et Tom",
            iconKey = "travel",
            targetAmountCents = 800_00,
            isPriority = false,
            targetDate = null,
            createdDate = seededOn,
        )
        goalRepository.addContribution(lisbonneId, 450_00)
        goalRepository.setAutoSave(lisbonneId, 50_00, SavingsCadence.WEEKLY, today())

        // Link a daily challenge to Lisbonne so "Défis liés" shows live data and
        // completing a day on the Challenge screen credits this goal.
        val active = challengeRepository.observeActive().first()
        if (active == null) {
            challengeRepository.create(
                title = "7 jours sans café",
                subtitle = "Économise 21 € en une semaine",
                dailyAmountCents = 300,
                totalDays = 7,
                startDate = today(),
                goalId = lisbonneId,
            )
        } else {
            challengeRepository.linkToGoal(active.id, lisbonneId)
        }
    }

    private fun Goal.toListItem(): GoalListItem {
        val ratio = progressOf(savedAmountCents, targetAmountCents)
        val remaining = (targetAmountCents - savedAmountCents).coerceAtLeast(0)
        return GoalListItem(
            id = id,
            iconKey = iconKey,
            name = name,
            savedLabel = formatEuros(savedAmountCents),
            targetLabel = "/ ${formatEuros(targetAmountCents)}",
            progress = ratio,
            percentLabel = "${(ratio * 100).toInt()}% complété",
            remainingLabel = "Il reste ${formatEuros(remaining)}",
            isPriority = isPriority,
        )
    }

    private fun Goal.toDetailUiState(challenges: List<Challenge>): GoalDetailUiState {
        val ratio = progressOf(savedAmountCents, targetAmountCents)
        val remaining = (targetAmountCents - savedAmountCents).coerceAtLeast(0)
        val projection = computeProjection(this, today())
        return GoalDetailUiState(
            id = id,
            category = category,
            name = name,
            subtitle = subtitle,
            savedLabel = formatEuros(savedAmountCents),
            targetLabel = "sur ${formatEuros(targetAmountCents)}",
            progress = ratio,
            percentLabel = "${(ratio * 100).toInt()} % Atteint",
            remainingLabel = "${formatEuros(remaining)} Restant",
            projectionMessage = projection.message,
            projectionDate = projection.date,
            projectionBars = PROJECTION_BARS,
            linkedChallenges = challenges.map { it.toLinkedItem(name) },
            quickAddLabel = "Ajouter 10 € maintenant",
        )
    }

    private fun Challenge.toLinkedItem(goalName: String): LinkedChallengeItem {
        val total = dailyAmountCents * totalDays
        val started = completedDays > 0
        val shortGoal = goalName.substringBefore(' ')
        return LinkedChallengeItem(
            id = id,
            iconKey = iconKeyForChallenge(title),
            title = title,
            contributionLabel = "+${formatEuros(total)} vers $shortGoal",
            statusLabel = if (started) "Jour ${completedDays.coerceAtMost(totalDays)}/$totalDays" else null,
            isStarted = started,
        )
    }

    private fun computeProjection(goal: Goal, today: LocalDate): Projection {
        val remaining = (goal.targetAmountCents - goal.savedAmountCents).coerceAtLeast(0)
        if (remaining <= 0L) {
            return Projection("Objectif atteint, bravo 🎉", null)
        }
        val ratePerDay: Double = if (goal.autoSaveAmountCents != null && goal.autoSaveCadence != null) {
            goal.autoSaveAmountCents.toDouble() / goal.autoSaveCadence.days
        } else {
            val days = goal.createdDate.daysUntil(today).coerceAtLeast(1)
            goal.savedAmountCents.toDouble() / days
        }
        if (ratePerDay <= 0.0) {
            return Projection("Définis une auto-épargne pour voir ta projection.", null)
        }
        val daysToComplete = ceil(remaining / ratePerDay).toInt()
        val projectedDate = today.plus(daysToComplete, DateTimeUnit.DAY)
        return Projection("Tu atteindras ton objectif le", formatDate(projectedDate))
    }

    private fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private data class Projection(val message: String, val date: String?)

    companion object {
        private const val QUICK_ADD_CENTS = 10_00L
        private val PROJECTION_BARS = listOf(0.25f, 0.4f, 0.55f, 0.7f, 0.85f, 1f)

        private val FrenchMonths = listOf(
            "janvier", "février", "mars", "avril", "mai", "juin",
            "juillet", "août", "septembre", "octobre", "novembre", "décembre",
        )

        private fun progressOf(saved: Long, target: Long): Float =
            if (target > 0) (saved.toFloat() / target).coerceIn(0f, 1f) else 0f

        private fun formatDate(d: LocalDate): String =
            "${d.day} ${FrenchMonths[d.month.ordinal]} ${d.year}"

        private fun iconKeyForChallenge(title: String): String = when {
            title.contains("café", ignoreCase = true) -> "coffee"
            title.contains("achat", ignoreCase = true) -> "shopping"
            else -> "gift"
        }

        /** Groups thousands with a space (French style), no decimals — e.g. 125000 -> "1 250 €". */
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
