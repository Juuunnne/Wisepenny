package com.wisepenny.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisepenny.presentation.goal.goalEmoji
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyShapes
import com.wisepenny.presentation.theme.WisepennyTheme
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel

/** Entry point used by the startup gate: wires the ViewModel to [OnboardingScreen]. */
@Composable
fun OnboardingRoot() {
    val viewModel = koinViewModel<OnboardingViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    OnboardingScreen(
        state = state,
        onCurrencySelect = viewModel::onCurrencySelect,
        onMotivationSelect = viewModel::onMotivationSelect,
        onFirstNameChange = viewModel::onFirstNameChange,
        onNameChange = viewModel::onNameChange,
        onAmountChange = viewModel::onAmountChange,
        onHorizonSelect = viewModel::onHorizonSelect,
        onBack = viewModel::back,
        onNext = viewModel::next,
        onSkip = viewModel::skip,
    )
}

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onCurrencySelect: (String) -> Unit,
    onMotivationSelect: (OnboardingMotivation) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onHorizonSelect: (Int) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WisepennyColors.BackgroundPrimary)
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.huge, bottom = Spacing.xl),
    ) {
        StepIndicator(step = state.step)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            when (state.step) {
                OnboardingStep.WELCOME -> WelcomeStep(state, onFirstNameChange)
                OnboardingStep.CURRENCY -> CurrencyStep(state, onCurrencySelect)
                OnboardingStep.MOTIVATION -> MotivationStep(state, onMotivationSelect)
                OnboardingStep.GOAL -> GoalStep(state, onNameChange, onAmountChange)
                OnboardingStep.PREVIEW -> PreviewStep(state, onHorizonSelect)
                OnboardingStep.NOTIFICATIONS -> NotificationsStep()
                OnboardingStep.BANK -> BankStep()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!state.isFirstStep) {
                    TextButton(onClick = onBack) {
                        Text(
                            text = "Retour",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WisepennyColors.TextTertiary,
                        )
                    }
                }
                Button(
                    onClick = onNext,
                    enabled = state.canProceed,
                    shape = WisepennyShapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WisepennyColors.AccentMint,
                        contentColor = WisepennyColors.TextOnLight,
                        disabledContainerColor = WisepennyColors.BorderSubtle,
                        disabledContentColor = WisepennyColors.TextDisabled,
                    ),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = primaryLabel(state), style = MaterialTheme.typography.titleMedium)
                }
            }
            if (state.hasSkip) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        text = skipLabel(state),
                        style = MaterialTheme.typography.bodyMedium,
                        color = WisepennyColors.TextTertiary,
                    )
                }
            }
        }
    }
}

private fun primaryLabel(state: OnboardingUiState): String = when (state.step) {
    OnboardingStep.WELCOME -> "Commencer"
    OnboardingStep.CURRENCY -> "Confirmer"
    OnboardingStep.NOTIFICATIONS -> "Activer les rappels"
    OnboardingStep.BANK -> if (state.isSubmitting) "Création…" else "Connecter (démo)"
    else -> "Continuer"
}

private fun skipLabel(state: OnboardingUiState): String = when (state.step) {
    OnboardingStep.NOTIFICATIONS -> "Plus tard"
    else -> "Ignorer pour l'instant"
}

/**
 * The wizard's first step: the brand intro plus an optional first-name field. The
 * name feeds the dashboard greeting; left blank, the greeting falls back to a neutral
 * label. Kept separate from [WelcomeContent] so the Profil "Revoir l'introduction"
 * overlay reuses the intro without showing an input.
 */
@Composable
private fun WelcomeStep(state: OnboardingUiState, onFirstNameChange: (String) -> Unit) {
    WelcomeContent()
    WisepennyTextField(
        value = state.firstName,
        onValueChange = onFirstNameChange,
        placeholder = "Ton prénom (facultatif)",
    )
}

/**
 * The brand intro shown as the wizard's first step and reused read-only by the
 * Profil "Revoir l'introduction" overlay.
 */
@Composable
fun WelcomeContent() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text(
            text = "Bienvenue sur Wisepenny",
            style = MaterialTheme.typography.headlineMedium,
            color = WisepennyColors.TextPrimary,
        )
        Text(
            text = "Apprends la finance et regarde ton objectif grandir, un petit pas à la fois.",
            style = MaterialTheme.typography.titleMedium,
            color = WisepennyColors.AccentMint,
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            WelcomeBullet("🎯", "Fixe un objectif d'épargne concret")
            WelcomeBullet("📚", "Apprends avec des modules courts et clairs")
            WelcomeBullet("📈", "Suis ta progression jour après jour")
        }
    }
}

@Composable
private fun WelcomeBullet(emoji: String, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = emoji, style = MaterialTheme.typography.titleMedium)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = WisepennyColors.TextSecondary,
        )
    }
}

@Composable
private fun CurrencyStep(state: OnboardingUiState, onCurrencySelect: (String) -> Unit) {
    StepHeading("Ta devise", "On l'a détectée pour toi — confirme ou change.")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WisepennyShapes.large)
            .background(WisepennyColors.SurfaceElevated)
            .padding(Spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "${currencySymbol(state.currency)}  ${state.currency}",
            style = MaterialTheme.typography.displayMedium,
            color = WisepennyColors.TextPrimary,
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        ONBOARDING_CURRENCIES.forEach { code ->
            SelectChip(
                text = "${currencySymbol(code)} $code",
                selected = state.currency == code,
                onClick = { onCurrencySelect(code) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MotivationStep(
    state: OnboardingUiState,
    onMotivationSelect: (OnboardingMotivation) -> Unit,
) {
    StepHeading("Pourquoi épargnes-tu ?", "Choisis ce qui te ressemble le plus.")
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        OnboardingMotivation.ALL.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                rowItems.forEach { motivation ->
                    SelectChip(
                        text = "${goalEmoji(motivation.iconKey)}  ${motivation.label}",
                        selected = state.motivation == motivation,
                        onClick = { onMotivationSelect(motivation) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GoalStep(
    state: OnboardingUiState,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
) {
    StepHeading("Ton objectif", "Donne-lui un nom et un montant à atteindre.")
    if (state.motivation != null) {
        Text(
            text = "Pour : ${state.motivation.label}",
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.AccentMint,
        )
    }
    WisepennyTextField(
        value = state.name,
        onValueChange = onNameChange,
        placeholder = "Ex. Voyage au Japon",
    )
    WisepennyTextField(
        value = state.amountText,
        onValueChange = onAmountChange,
        placeholder = "Montant cible",
        keyboardType = KeyboardType.Number,
        suffix = currencySymbol(state.currency),
    )
}

@Composable
private fun PreviewStep(state: OnboardingUiState, onHorizonSelect: (Int) -> Unit) {
    StepHeading("Ton plan d'épargne", "Choisis une échéance, on calcule le reste.")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        ONBOARDING_HORIZONS.forEach { months ->
            SelectChip(
                text = "$months mois",
                selected = state.horizonMonths == months,
                onClick = { onHorizonSelect(months) },
                modifier = Modifier.weight(1f),
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WisepennyShapes.large)
            .background(WisepennyColors.AccentMintSoft)
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = "Épargne",
            style = MaterialTheme.typography.bodyMedium,
            color = WisepennyColors.TextOnLightMuted,
        )
        Text(
            text = "${formatMoney(state.monthlyCents, state.currency)} / mois",
            style = MaterialTheme.typography.displayMedium,
            color = WisepennyColors.TextOnLight,
        )
        Text(
            text = "🎉 Objectif atteint en ${formatMonthYear(state.projectedDate)}",
            style = MaterialTheme.typography.titleMedium,
            color = WisepennyColors.TextOnLight,
            modifier = Modifier.padding(top = Spacing.xs),
        )
    }
}

@Composable
private fun NotificationsStep() {
    PrimingContent(
        emoji = "🔔",
        title = "Reste sur la bonne voie",
        body = "On t'enverra un rappel en douceur quand un défi t'attend ou que tu " +
            "approches de ton objectif. Pas de spam, promis.",
    )
}

@Composable
private fun BankStep() {
    PrimingContent(
        emoji = "🏦",
        title = "Connecte ta banque",
        body = "Pour la démo, Wisepenny utilise une connexion bancaire simulée : " +
            "aucune vraie banque n'est reliée et aucune donnée réelle n'est partagée. " +
            "Tu peux ignorer cette étape.",
    )
}

@Composable
private fun PrimingContent(emoji: String, title: String, body: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(text = emoji, style = MaterialTheme.typography.displayMedium)
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = WisepennyColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = WisepennyColors.TextTertiary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StepHeading(title: String, subtitle: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = WisepennyColors.TextPrimary,
    )
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = WisepennyColors.TextTertiary,
    )
}

@Composable
private fun SelectChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = if (selected) WisepennyColors.AccentMintSoft else WisepennyColors.SurfaceElevated
    val content = if (selected) WisepennyColors.TextOnLight else WisepennyColors.TextSecondary
    val borderColor = if (selected) WisepennyColors.AccentMint else WisepennyColors.BorderSubtle
    Box(
        modifier = modifier
            .clip(WisepennyShapes.small)
            .background(container)
            .border(1.dp, borderColor, WisepennyShapes.small)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = content,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun WisepennyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    suffix: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextDisabled,
            )
        },
        suffix = suffix?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WisepennyColors.TextTertiary,
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = WisepennyShapes.small,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = WisepennyColors.TextPrimary,
            unfocusedTextColor = WisepennyColors.TextPrimary,
            focusedBorderColor = WisepennyColors.AccentMint,
            unfocusedBorderColor = WisepennyColors.BorderSubtle,
            cursorColor = WisepennyColors.AccentMint,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun StepIndicator(step: OnboardingStep) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        OnboardingStep.entries.forEach { s ->
            val active = s.ordinal <= step.ordinal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (active) WisepennyColors.AccentMint else WisepennyColors.BorderSubtle),
            )
        }
    }
}

private fun currencySymbol(code: String): String = when (code) {
    "USD" -> "$"
    "GBP" -> "£"
    else -> "€"
}

/** French thousands grouping with the currency symbol — e.g. 4000 -> "40 €". */
private fun formatMoney(cents: Long, currency: String): String {
    val units = cents / 100
    val grouped = units.toString()
        .reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()
    return "$grouped ${currencySymbol(currency)}"
}

private val FrenchMonths = listOf(
    "janvier", "février", "mars", "avril", "mai", "juin",
    "juillet", "août", "septembre", "octobre", "novembre", "décembre",
)

private fun formatMonthYear(d: LocalDate): String = "${FrenchMonths[d.month.ordinal]} ${d.year}"

@Preview
@Composable
private fun OnboardingPreviewStepPreview() {
    WisepennyTheme {
        OnboardingScreen(
            state = OnboardingUiState(
                today = LocalDate(2026, 7, 9),
                step = OnboardingStep.PREVIEW,
                motivation = OnboardingMotivation.ALL.first(),
                name = "Voyage au Japon",
                amountText = "3000",
                horizonMonths = 12,
            ),
            onCurrencySelect = {},
            onMotivationSelect = {},
            onFirstNameChange = {},
            onNameChange = {},
            onAmountChange = {},
            onHorizonSelect = {},
            onBack = {},
            onNext = {},
            onSkip = {},
        )
    }
}
