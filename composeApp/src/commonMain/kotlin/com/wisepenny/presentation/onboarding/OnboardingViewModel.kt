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

/**
 * The onboarding wizard's screens, in order. [WELCOME] (the landing page) and [DONE]
 * (the completion screen) sit outside the numbered progress; [CURRENCY]…[BANK] are the
 * seven numbered steps shown as "ÉTAPE X SUR 7".
 */
enum class OnboardingStep { WELCOME, CURRENCY, MOTIVATION, NAME, GOAL_CATEGORY, AMOUNT, NOTIFICATIONS, BANK, DONE }

/** A currency offered on step 1. [name] is the French label; the symbol is derived. */
data class OnboardingCurrency(val code: String, val name: String)

/** A saving motivation (step 2). Stored on the profile; decoupled from the goal category. */
data class OnboardingMotivation(val label: String, val subtitle: String, val iconKey: String) {
    companion object {
        val ALL = listOf(
            OnboardingMotivation("Réaliser un projet", "Un voyage, un achat, un grand jour", "target"),
            OnboardingMotivation("Me sentir en sécurité", "Un matelas pour les imprévus", "home"),
            OnboardingMotivation("Reprendre le contrôle", "Savoir enfin où va mon argent", "target"),
            OnboardingMotivation("Dépenser moins sur un coup de tête", "Rompre avec les achats impulsifs", "shopping"),
            OnboardingMotivation("Préparer l'avenir", "Épargner pour plus tard", "target"),
        )
    }
}

/**
 * A goal category (step 4). Drives the created goal's category, icon and auto-generated
 * name — there is no manual name field.
 */
data class OnboardingCategory(val label: String, val iconKey: String, val defaultGoalName: String) {
    companion object {
        val ALL = listOf(
            OnboardingCategory("Voyage", "travel", "Mon prochain voyage"),
            OnboardingCategory("Précaution", "target", "Mon matelas de sécurité"),
            OnboardingCategory("Logement", "home", "Mon logement"),
            OnboardingCategory("Événement", "gift", "Mon grand événement"),
            OnboardingCategory("Gros achat", "shopping", "Mon gros achat"),
            OnboardingCategory("Autre", "target", "Mon objectif"),
        )
    }
}

/** Currencies offered on step 1; EUR is the auto-detected default. */
val ONBOARDING_CURRENCIES = listOf(
    OnboardingCurrency("EUR", "Euro"),
    OnboardingCurrency("USD", "Dollar américain"),
    OnboardingCurrency("GBP", "Livre sterling"),
    OnboardingCurrency("CHF", "Franc suisse"),
    OnboardingCurrency("CAD", "Dollar canadien"),
)

/** Quick-pick target amounts (whole units) offered on the amount step. */
val ONBOARDING_PRESETS = listOf(500L, 1000L, 3000L, 5000L)

/** The single fixed savings horizon, in months, used for the plan projection. */
const val ONBOARDING_HORIZON_MONTHS = 6

/** Step of the amount stepper, and the floor it clamps to (whole units). */
private const val AMOUNT_STEP = 100L
private const val AMOUNT_MIN = 100L

/** Demo institutions offered on the bank step. Selecting any just marks the bank linked. */
val ONBOARDING_BANKS = listOf(
    "Banque Sandbox" to "Établissement de démonstration",
    "Néo Démo" to "Néobanque simulée",
    "Livret Démo" to "Compte épargne fictif",
)

data class OnboardingUiState(
    val today: LocalDate,
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val currency: String = "EUR",
    val motivation: OnboardingMotivation? = null,
    val firstName: String = "",
    val category: OnboardingCategory? = null,
    val amountUnits: Long = 3000,
    val selectedBank: String? = ONBOARDING_BANKS.first().first,
    val notificationsOptIn: Boolean = false,
    val bankLinked: Boolean = false,
    val isSubmitting: Boolean = false,
) {
    /** Target in cents. */
    val amountCents: Long = amountUnits * 100

    /** Monthly contribution needed to reach the target within the fixed horizon. */
    val monthlyCents: Long =
        if (amountCents > 0) ceil(amountCents.toDouble() / ONBOARDING_HORIZON_MONTHS).toLong() else 0L

    /** Rough weekly contribution (~26 weeks over 6 months) shown as an estimate. */
    val weeklyCents: Long =
        if (amountCents > 0) ceil(amountCents.toDouble() / 26).toLong() else 0L

    /** The date the goal is reached at the chosen pace. */
    val projectedDate: LocalDate = today.plus(ONBOARDING_HORIZON_MONTHS, DateTimeUnit.MONTH)

    /** Auto-generated goal name from the chosen category. */
    val goalName: String = category?.defaultGoalName ?: "Mon objectif"

    val canProceed: Boolean = when (step) {
        OnboardingStep.MOTIVATION -> motivation != null
        OnboardingStep.GOAL_CATEGORY -> category != null
        OnboardingStep.AMOUNT -> amountCents > 0
        OnboardingStep.BANK -> !isSubmitting
        OnboardingStep.DONE -> !isSubmitting
        else -> true
    }

    /** Whether the current step shows the numbered progress bar ("ÉTAPE X SUR 7"). */
    val isNumberedStep: Boolean =
        step != OnboardingStep.WELCOME && step != OnboardingStep.DONE

    /** 1-based index of the current numbered step (CURRENCY = 1 … BANK = 7). */
    val stepNumber: Int = step.ordinal // CURRENCY is ordinal 1 … BANK is ordinal 7

    /** The soft-ask steps carry a secondary "skip" action alongside the primary. */
    val hasSkip: Boolean = step == OnboardingStep.NOTIFICATIONS || step == OnboardingStep.BANK
}

/** Total number of numbered steps (CURRENCY … BANK). */
const val ONBOARDING_TOTAL_STEPS = 7

/**
 * Drives the onboarding wizard. On completion it seeds the demo world, creates the
 * user's real goal on top (as the single priority goal, with a target date and a
 * monthly auto-save from the fixed horizon), then writes the profile with
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

    fun onCategorySelect(category: OnboardingCategory) =
        _state.update { it.copy(category = category) }

    fun onAmountIncrement() =
        _state.update { it.copy(amountUnits = it.amountUnits + AMOUNT_STEP) }

    fun onAmountDecrement() =
        _state.update { it.copy(amountUnits = (it.amountUnits - AMOUNT_STEP).coerceAtLeast(AMOUNT_MIN)) }

    fun onAmountPreset(units: Long) = _state.update { it.copy(amountUnits = units) }

    fun onBankSelect(name: String) = _state.update { it.copy(selectedBank = name) }

    /** The primary (affirmative) action for the current step. */
    fun next() {
        val current = _state.value
        if (!current.canProceed) return
        when (current.step) {
            OnboardingStep.NOTIFICATIONS ->
                _state.update { it.copy(notificationsOptIn = true, step = OnboardingStep.BANK) }
            OnboardingStep.BANK ->
                _state.update { it.copy(bankLinked = true, step = OnboardingStep.DONE) }
            OnboardingStep.DONE -> complete()
            else ->
                _state.update { it.copy(step = OnboardingStep.entries[it.step.ordinal + 1]) }
        }
    }

    /** The secondary (skip) action, only meaningful on the two soft-ask steps. */
    fun skip() {
        when (_state.value.step) {
            OnboardingStep.NOTIFICATIONS ->
                _state.update { it.copy(notificationsOptIn = false, step = OnboardingStep.BANK) }
            OnboardingStep.BANK ->
                _state.update { it.copy(bankLinked = false, step = OnboardingStep.DONE) }
            else -> Unit
        }
    }

    fun back() {
        _state.update {
            if (it.step == OnboardingStep.WELCOME) it
            else it.copy(step = OnboardingStep.entries[it.step.ordinal - 1])
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
                name = s.goalName,
                category = s.category?.label ?: "Objectif",
                subtitle = s.motivation?.label ?: "",
                iconKey = s.category?.iconKey ?: "target",
                targetAmountCents = s.amountCents,
                isPriority = true,
                targetDate = s.projectedDate,
                createdDate = today,
            )
            // Turn the fixed horizon into a real monthly auto-save so the dashboard
            // projection matches the payoff implied by the wizard.
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
