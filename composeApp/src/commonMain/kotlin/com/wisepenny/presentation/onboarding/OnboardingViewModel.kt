package com.wisepenny.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisepenny.data.seed.DataSeeder
import com.wisepenny.domain.model.SavingsCadence
import com.wisepenny.domain.repository.GoalRepository
import com.wisepenny.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

/** The seven full-screen steps of the first-launch wizard, in order. */
enum class OnboardingStep { WELCOME, CURRENCY, MOTIVATION, GOAL, PREVIEW, NOTIFICATIONS, BANK }

/** A reason to save. Drives the goal's category and icon so there's no separate picker. */
data class OnboardingMotivation(val label: String, val iconKey: String, val category: String) {
    companion object {
        val ALL = listOf(
            OnboardingMotivation("Voyager", "travel", "Voyage"),
            OnboardingMotivation("Un gros achat", "shopping", "Achat"),
            OnboardingMotivation("Me faire plaisir", "gift", "Plaisir"),
            OnboardingMotivation("Une voiture", "car", "Voiture"),
            OnboardingMotivation("Un logement", "home", "Logement"),
            OnboardingMotivation("Un projet perso", "target", "Projet"),
        )
    }
}

/** Currencies offered on the confirm step; EUR is the auto-detected default. */
val ONBOARDING_CURRENCIES = listOf("EUR", "USD", "GBP")

/** Deadlines offered on the preview step, in months. */
val ONBOARDING_HORIZONS = listOf(6, 12, 24)

data class OnboardingUiState(
    val today: LocalDate,
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val currency: String = "EUR",
    val motivation: OnboardingMotivation? = null,
    val firstName: String = "",
    val name: String = "",
    val amountText: String = "",
    val horizonMonths: Int = 12,
    val notificationsOptIn: Boolean = false,
    val bankLinked: Boolean = false,
    val isSubmitting: Boolean = false,
) {
    /** Target in cents, from the digits typed (whole units). */
    val amountCents: Long = amountText.filter { it.isDigit() }.toLongOrNull()?.times(100) ?: 0L

    /** Monthly contribution needed to reach the target within [horizonMonths]. */
    val monthlyCents: Long =
        if (amountCents > 0 && horizonMonths > 0) {
            ceil(amountCents.toDouble() / horizonMonths).toLong()
        } else 0L

    /** The date the goal is reached at the chosen pace. */
    val projectedDate: LocalDate = today.plus(horizonMonths, DateTimeUnit.MONTH)

    val canProceed: Boolean = when (step) {
        OnboardingStep.MOTIVATION -> motivation != null
        OnboardingStep.GOAL -> name.isNotBlank() && amountCents > 0
        OnboardingStep.BANK -> !isSubmitting
        else -> true
    }

    val isFirstStep: Boolean = step == OnboardingStep.WELCOME
    val isLastStep: Boolean = step == OnboardingStep.BANK

    /** The soft-ask steps carry a secondary "skip" action alongside the primary. */
    val hasSkip: Boolean = step == OnboardingStep.NOTIFICATIONS || step == OnboardingStep.BANK
}

/**
 * Drives the onboarding wizard. On completion it seeds the demo world, creates the
 * user's real goal on top (as the single priority goal, with a target date and a
 * monthly auto-save from the chosen deadline), then writes the profile with
 * onboardingCompleted = true — which the startup gate in [com.wisepenny.App] observes
 * to swap the wizard for the main app.
 */
class OnboardingViewModel(
    private val profileRepository: ProfileRepository,
    private val goalRepository: GoalRepository,
    private val dataSeeder: DataSeeder,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState(today = today()))
    val uiState: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun onCurrencySelect(code: String) = _state.update { it.copy(currency = code) }

    fun onMotivationSelect(motivation: OnboardingMotivation) =
        _state.update { it.copy(motivation = motivation) }

    fun onFirstNameChange(value: String) = _state.update { it.copy(firstName = value) }

    fun onNameChange(value: String) = _state.update { it.copy(name = value) }

    fun onAmountChange(value: String) =
        _state.update { it.copy(amountText = value.filter { c -> c.isDigit() }) }

    fun onHorizonSelect(months: Int) = _state.update { it.copy(horizonMonths = months) }

    /** The primary (affirmative) action for the current step. */
    fun next() {
        val current = _state.value
        if (!current.canProceed) return
        when (current.step) {
            OnboardingStep.NOTIFICATIONS ->
                _state.update { it.copy(notificationsOptIn = true, step = OnboardingStep.BANK) }
            OnboardingStep.BANK -> {
                _state.update { it.copy(bankLinked = true) }
                complete()
            }
            else ->
                _state.update { it.copy(step = OnboardingStep.entries[it.step.ordinal + 1]) }
        }
    }

    /** The secondary (skip) action, only meaningful on the two soft-ask steps. */
    fun skip() {
        when (_state.value.step) {
            OnboardingStep.NOTIFICATIONS ->
                _state.update { it.copy(notificationsOptIn = false, step = OnboardingStep.BANK) }
            OnboardingStep.BANK -> {
                _state.update { it.copy(bankLinked = false) }
                complete()
            }
            else -> Unit
        }
    }

    fun back() {
        _state.update {
            if (it.isFirstStep) it else it.copy(step = OnboardingStep.entries[it.step.ordinal - 1])
        }
    }

    private fun complete() {
        val s = _state.value
        if (s.isSubmitting) return
        _state.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val today = s.today
            // Demo world first (guarded on empty goals), so the seeder doesn't skip.
            dataSeeder.seedDemoWorld(today)
            val goalId = goalRepository.create(
                name = s.name.trim(),
                category = s.motivation?.category ?: "Objectif",
                subtitle = s.motivation?.label ?: "",
                iconKey = s.motivation?.iconKey ?: "target",
                targetAmountCents = s.amountCents,
                isPriority = true,
                targetDate = s.projectedDate,
                createdDate = today,
            )
            // Turn the chosen deadline into a real monthly auto-save so the
            // dashboard projection matches the payoff shown in the wizard.
            if (s.monthlyCents > 0) {
                goalRepository.setAutoSave(goalId, s.monthlyCents, SavingsCadence.MONTHLY, today)
            }
            profileRepository.save(
                firstName = s.firstName.trim(),
                motivation = s.motivation?.label ?: "",
                createdDate = today,
                onboardingCompleted = true,
                currency = s.currency,
                notificationsOptIn = s.notificationsOptIn,
                bankLinked = s.bankLinked,
            )
            // No need to reset isSubmitting: writing the profile flips the startup
            // gate to the main app and this ViewModel is disposed.
        }
    }

    private fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}
