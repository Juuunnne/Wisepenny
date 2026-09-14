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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisepenny.presentation.components.SavingsProgressBar
import com.wisepenny.presentation.components.WisepennyCard
import com.wisepenny.presentation.components.WisepennyCardVariant
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
        onCategorySelect = viewModel::onCategorySelect,
        onAmountIncrement = viewModel::onAmountIncrement,
        onAmountDecrement = viewModel::onAmountDecrement,
        onAmountPreset = viewModel::onAmountPreset,
        onBankSelect = viewModel::onBankSelect,
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
    onCategorySelect: (OnboardingCategory) -> Unit,
    onAmountIncrement: () -> Unit,
    onAmountDecrement: () -> Unit,
    onAmountPreset: (Long) -> Unit,
    onBankSelect: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    when (state.step) {
        OnboardingStep.WELCOME -> WelcomeScreen(onNext)
        OnboardingStep.DONE -> DoneScreen(state, onNext)
        else -> NumberedStepScaffold(state, onBack, onNext, onSkip) {
            when (state.step) {
                OnboardingStep.CURRENCY -> CurrencyStep(state, onCurrencySelect)
                OnboardingStep.MOTIVATION -> MotivationStep(state, onMotivationSelect)
                OnboardingStep.NAME -> NameStep(state, onFirstNameChange)
                OnboardingStep.GOAL_CATEGORY -> GoalCategoryStep(state, onCategorySelect)
                OnboardingStep.AMOUNT -> AmountStep(state, onAmountIncrement, onAmountDecrement, onAmountPreset)
                OnboardingStep.NOTIFICATIONS -> NotificationsStep()
                OnboardingStep.BANK -> BankStep(state, onBankSelect)
                else -> Unit
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Frames
// ---------------------------------------------------------------------------

/** The shared frame for the seven numbered steps: back + progress, body, pinned action. */
@Composable
private fun NumberedStepScaffold(
    state: OnboardingUiState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    body: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WisepennyColors.BackgroundPrimary)
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.xxl, bottom = Spacing.xl),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackButton(onBack)
            OnboardingProgressBar(
                fraction = state.stepNumber / ONBOARDING_TOTAL_STEPS.toFloat(),
                modifier = Modifier.weight(1f),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            StepHeader(state)
            body()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            PrimaryButton(
                text = primaryLabel(state),
                enabled = state.canProceed,
                onClick = onNext,
            )
            if (state.hasSkip) {
                TextButton(onClick = onSkip) {
                    Text(
                        text = skipLabel(state),
                        style = MaterialTheme.typography.titleMedium,
                        color = WisepennyColors.AccentMint,
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeScreen(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WisepennyColors.BackgroundPrimary)
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.xxl, bottom = Spacing.xl),
    ) {
        WelcomeContent()
        Spacer(Modifier.weight(1f))
        PrimaryButton(text = "Commencer", enabled = true, onClick = onNext)
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = "Aucun compte requis — on personnalise l'app en 7 étapes.",
            style = MaterialTheme.typography.bodyMedium,
            color = WisepennyColors.TextTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.md),
        )
    }
}

/**
 * The brand intro shown at the top of the landing step and reused read-only by the
 * Profil "Revoir l'introduction" overlay — a sample goal card, the headline and pitch.
 */
@Composable
fun WelcomeContent() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        Text(
            text = "WISEPENNY",
            style = MaterialTheme.typography.titleMedium,
            color = WisepennyColors.AccentMint,
            fontWeight = FontWeight.ExtraBold,
        )
        GoalPreviewCard(
            title = "Voyage au Japon",
            savedCents = 125_000,
            targetCents = 300_000,
            currency = "EUR",
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(
                text = "Atteins tes objectifs, un défi à la fois.",
                style = MaterialTheme.typography.displayMedium,
                color = WisepennyColors.TextPrimary,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "Des petits défis quotidiens qui font grandir ton épargne, sans y penser.",
                style = MaterialTheme.typography.titleMedium,
                color = WisepennyColors.TextTertiary,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun DoneScreen(state: OnboardingUiState, onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WisepennyColors.BackgroundPrimary)
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.xxl, bottom = Spacing.xl),
    ) {
        Spacer(Modifier.weight(0.6f))
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(WisepennyColors.AccentMint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(WisepennyColors.AccentMint),
                contentAlignment = Alignment.Center,
            ) {
                Text("✓", fontSize = 36.sp, color = WisepennyColors.TextOnLight, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = if (state.firstName.isBlank()) "Bienvenue !" else "Bienvenue, ${state.firstName.trim()} !",
            style = MaterialTheme.typography.displayMedium,
            color = WisepennyColors.TextPrimary,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "Ton premier objectif est prêt. Ton premier défi t'attend déjà.",
            style = MaterialTheme.typography.titleMedium,
            color = WisepennyColors.TextTertiary,
        )
        Spacer(Modifier.height(Spacing.xl))
        GoalPreviewCard(
            title = state.goalName,
            savedCents = 0,
            targetCents = state.amountCents,
            currency = state.currency,
        )
        Spacer(Modifier.weight(1f))
        PrimaryButton(
            text = if (state.isSubmitting) "Création…" else "C'est parti",
            enabled = !state.isSubmitting,
            onClick = onNext,
        )
    }
}

// ---------------------------------------------------------------------------
// Numbered steps
// ---------------------------------------------------------------------------

@Composable
private fun CurrencyStep(state: OnboardingUiState, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        ONBOARDING_CURRENCIES.forEach { c ->
            val selected = state.currency == c.code
            OptionRow(
                selected = selected,
                onClick = { onSelect(c.code) },
                leading = { SymbolBadge(currencySymbol(c.code), selected) },
                title = c.name,
                subtitle = c.code,
            )
        }
    }
}

@Composable
private fun MotivationStep(state: OnboardingUiState, onSelect: (OnboardingMotivation) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        OnboardingMotivation.ALL.forEach { m ->
            val selected = state.motivation == m
            OptionRow(
                selected = selected,
                onClick = { onSelect(m) },
                leading = { EmojiBadge(goalEmoji(m.iconKey)) },
                title = m.label,
                subtitle = m.subtitle,
            )
        }
    }
}

@Composable
private fun NameStep(state: OnboardingUiState, onFirstNameChange: (String) -> Unit) {
    Text(
        text = "PRÉNOM",
        style = MaterialTheme.typography.labelSmall,
        color = WisepennyColors.TextTertiary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = Spacing.sm),
    )
    WisepennyTextField(
        value = state.firstName,
        onValueChange = onFirstNameChange,
        placeholder = "Ton prénom",
    )
}

@Composable
private fun GoalCategoryStep(state: OnboardingUiState, onSelect: (OnboardingCategory) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        OnboardingCategory.ALL.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                rowItems.forEach { category ->
                    CategoryCard(
                        category = category,
                        selected = state.category == category,
                        onClick = { onSelect(category) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AmountStep(
    state: OnboardingUiState,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onPreset: (Long) -> Unit,
) {
    Spacer(Modifier.height(Spacing.xl))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperButton("−", onDecrement)
        Text(
            text = formatMoney(state.amountCents, state.currency),
            style = MaterialTheme.typography.displayMedium,
            color = WisepennyColors.TextPrimary,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        StepperButton("+", onIncrement)
    }
    Spacer(Modifier.height(Spacing.lg))
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        ONBOARDING_PRESETS.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
            ) {
                rowItems.forEach { units ->
                    PresetChip(
                        text = formatMoney(units * 100, state.currency),
                        selected = state.amountUnits == units,
                        onClick = { onPreset(units) },
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(Spacing.lg))
    Text(
        text = buildAnnotatedString {
            append("Soit environ ")
            withStyle(SpanStyle(color = WisepennyColors.AccentMint, fontWeight = FontWeight.Bold)) {
                append("${formatMoney(state.weeklyCents, state.currency)} par semaine")
            }
            append(" pendant $ONBOARDING_HORIZON_MONTHS mois.")
        },
        style = MaterialTheme.typography.bodyMedium,
        color = WisepennyColors.TextTertiary,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun NotificationsStep() {
    Spacer(Modifier.height(Spacing.md))
    MockNotificationCard()
}

@Composable
private fun BankStep(state: OnboardingUiState, onBankSelect: (String) -> Unit) {
    DemoCallout()
    Spacer(Modifier.height(Spacing.xs))
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        ONBOARDING_BANKS.forEach { (name, subtitle) ->
            val selected = state.selectedBank == name
            OptionRow(
                selected = selected,
                onClick = { onBankSelect(name) },
                leading = { LetterBadge(name.first().toString(), selected) },
                title = name,
                subtitle = subtitle,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Building blocks
// ---------------------------------------------------------------------------

private fun primaryLabel(state: OnboardingUiState): String = when (state.step) {
    OnboardingStep.NOTIFICATIONS -> "Activer les rappels"
    else -> "Continuer"
}

private fun skipLabel(state: OnboardingUiState): String = when (state.step) {
    OnboardingStep.NOTIFICATIONS -> "Plus tard"
    else -> "Ignorer cette étape"
}

private fun stepTitle(step: OnboardingStep): String = when (step) {
    OnboardingStep.CURRENCY -> "Confirme ta devise"
    OnboardingStep.MOTIVATION -> "Qu'est-ce qui te motive ?"
    OnboardingStep.NAME -> "Et toi, c'est comment ?"
    OnboardingStep.GOAL_CATEGORY -> "Ton premier objectif ?"
    OnboardingStep.AMOUNT -> "Combien veux-tu mettre de côté ?"
    OnboardingStep.NOTIFICATIONS -> "Un petit rappel chaque jour"
    OnboardingStep.BANK -> "Relier ta banque"
    else -> ""
}

private fun stepSubtitle(state: OnboardingUiState): String = when (state.step) {
    OnboardingStep.CURRENCY -> "Toutes tes économies seront affichées dans cette devise."
    OnboardingStep.MOTIVATION -> "On adapte tes défis et tes rappels selon ce qui compte pour toi."
    OnboardingStep.NAME -> "Juste ton prénom, pour personnaliser tes défis."
    OnboardingStep.GOAL_CATEGORY -> "Choisis une catégorie — tu pourras tout changer plus tard."
    OnboardingStep.AMOUNT -> "Pour « ${state.goalName} ». Une estimation suffit."
    OnboardingStep.NOTIFICATIONS -> "Un seul par jour, au bon moment, pour ne jamais casser ta série."
    OnboardingStep.BANK -> "Pour créditer tes défis automatiquement — ou fais-le à la main plus tard."
    else -> ""
}

/** The uniform header for the numbered steps: étape label + big title + subtitle. */
@Composable
private fun StepHeader(state: OnboardingUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = "ÉTAPE ${state.stepNumber} SUR $ONBOARDING_TOTAL_STEPS",
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.AccentMint,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
        )
        Text(
            text = stepTitle(state.step),
            style = MaterialTheme.typography.displayMedium,
            color = WisepennyColors.TextPrimary,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = stepSubtitle(state),
            style = MaterialTheme.typography.titleMedium,
            color = WisepennyColors.TextTertiary,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
private fun PrimaryButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = WisepennyShapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = WisepennyColors.AccentMint,
            contentColor = WisepennyColors.TextOnLight,
            disabledContainerColor = WisepennyColors.BorderSubtle,
            disabledContentColor = WisepennyColors.TextDisabled,
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = Spacing.md),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onBack),
        contentAlignment = Alignment.Center,
    ) {
        Text("←", fontSize = 24.sp, color = WisepennyColors.TextPrimary)
    }
}

@Composable
private fun OnboardingProgressBar(fraction: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(6.dp)
            .clip(RoundedCornerShape(50))
            .background(WisepennyColors.BorderSubtle),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(WisepennyColors.AccentMint),
        )
    }
}

/** A selectable list row: leading badge, title, optional subtitle, trailing check. */
@Composable
private fun OptionRow(
    selected: Boolean,
    onClick: () -> Unit,
    leading: @Composable () -> Unit,
    title: String,
    subtitle: String? = null,
) {
    val border = if (selected) WisepennyColors.AccentMint else WisepennyColors.BorderSubtle
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WisepennyShapes.medium)
            .background(WisepennyColors.SurfaceElevated)
            .border(if (selected) 1.5.dp else 1.dp, border, WisepennyShapes.medium)
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading()
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = WisepennyColors.TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WisepennyColors.TextTertiary,
                )
            }
        }
        CheckCircle(selected)
    }
}

@Composable
private fun CheckCircle(selected: Boolean) {
    if (selected) {
        Box(
            modifier = Modifier.size(28.dp).clip(CircleShape).background(WisepennyColors.AccentMint),
            contentAlignment = Alignment.Center,
        ) {
            Text("✓", fontSize = 15.sp, color = WisepennyColors.TextOnLight, fontWeight = FontWeight.Bold)
        }
    } else {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .border(1.5.dp, WisepennyColors.BorderSubtle, CircleShape),
        )
    }
}

@Composable
private fun SymbolBadge(symbol: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (selected) WisepennyColors.AccentMint else WisepennyColors.SurfaceElevated)
            .then(
                if (selected) Modifier
                else Modifier.border(1.5.dp, WisepennyColors.BorderSubtle, CircleShape)
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) WisepennyColors.TextOnLight else WisepennyColors.TextPrimary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun EmojiBadge(emoji: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(WisepennyColors.BackgroundPrimary)
            .border(1.dp, WisepennyColors.BorderSubtle, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun LetterBadge(letter: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (selected) WisepennyColors.AccentMint else WisepennyColors.SurfaceElevated)
            .then(
                if (selected) Modifier
                else Modifier.border(1.5.dp, WisepennyColors.BorderSubtle, CircleShape)
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) WisepennyColors.TextOnLight else WisepennyColors.TextPrimary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun CategoryCard(
    category: OnboardingCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = if (selected) WisepennyColors.SurfaceLight else WisepennyColors.SurfaceElevated
    val labelColor = if (selected) WisepennyColors.TextOnLight else WisepennyColors.TextPrimary
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(WisepennyShapes.medium)
            .background(container)
            .then(
                if (selected) Modifier.border(2.dp, WisepennyColors.AccentMint, WisepennyShapes.medium)
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(Spacing.md),
    ) {
        EmojiBadge(goalEmoji(category.iconKey))
        if (selected) {
            Box(modifier = Modifier.align(Alignment.TopEnd)) { CheckCircle(true) }
        }
        Text(
            text = category.label,
            style = MaterialTheme.typography.titleMedium,
            color = labelColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomStart),
        )
    }
}

@Composable
private fun StepperButton(symbol: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .border(1.5.dp, WisepennyColors.BorderSubtle, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = symbol, fontSize = 28.sp, color = WisepennyColors.TextSecondary, fontWeight = FontWeight.Normal)
    }
}

@Composable
private fun PresetChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val border = if (selected) WisepennyColors.AccentMint else WisepennyColors.BorderSubtle
    val content = if (selected) WisepennyColors.AccentMint else WisepennyColors.TextSecondary
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(1.5.dp, border, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium, color = content, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MockNotificationCard() {
    WisepennyCard(variant = WisepennyCardVariant.Elevated) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.Top,
        ) {
            EmojiBadge("🪙")
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "WISEPENNY",
                        style = MaterialTheme.typography.labelSmall,
                        color = WisepennyColors.AccentMint,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "maintenant",
                        style = MaterialTheme.typography.labelSmall,
                        color = WisepennyColors.TextTertiary,
                    )
                }
                Text(
                    text = "Ton défi du jour t'attend",
                    style = MaterialTheme.typography.titleMedium,
                    color = WisepennyColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "2 € à mettre de côté en renonçant à un café.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WisepennyColors.TextTertiary,
                )
            }
        }
    }
}

@Composable
private fun DemoCallout() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WisepennyShapes.medium)
            .background(WisepennyColors.SurfaceElevated)
            .border(1.dp, WisepennyColors.AccentMint, WisepennyShapes.medium)
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(WisepennyColors.AccentMint)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
        ) {
            Text(
                text = "DÉMO",
                style = MaterialTheme.typography.labelSmall,
                color = WisepennyColors.TextOnLight,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = buildAnnotatedString {
                append("Liaison ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("simulée") }
                append(" : aucune banque n'est réellement contactée et aucune donnée ")
                append("bancaire n'est collectée. Tu peux ignorer cette étape.")
            },
            style = MaterialTheme.typography.bodyMedium,
            color = WisepennyColors.TextSecondary,
        )
    }
}

/** The white goal card shown on the landing and completion screens. */
@Composable
private fun GoalPreviewCard(title: String, savedCents: Long, targetCents: Long, currency: String) {
    val progress = if (targetCents > 0) savedCents.toFloat() / targetCents else 0f
    WisepennyCard(variant = WisepennyCardVariant.Light) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(WisepennyColors.AccentMintSoft),
                contentAlignment = Alignment.Center,
            ) {
                Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(WisepennyColors.TextOnLight))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = WisepennyColors.TextOnLight,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(WisepennyColors.SurfaceLightAlt)
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
            ) {
                Text(
                    text = "Priorité",
                    style = MaterialTheme.typography.labelSmall,
                    color = WisepennyColors.TextOnLightMuted,
                )
            }
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = formatMoney(savedCents, currency),
                style = MaterialTheme.typography.headlineMedium,
                color = WisepennyColors.TextOnLight,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = " / ${formatMoney(targetCents, currency)}",
                style = MaterialTheme.typography.titleMedium,
                color = WisepennyColors.TextOnLightMuted,
            )
        }
        SavingsProgressBar(
            progress = progress,
            trackColor = WisepennyColors.SurfaceLightAlt,
            fillColor = WisepennyColors.AccentMintPressed,
            modifier = Modifier.padding(top = Spacing.xs),
        )
    }
}

@Composable
private fun WisepennyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.titleMedium,
                color = WisepennyColors.TextDisabled,
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = MaterialTheme.typography.titleMedium,
        shape = WisepennyShapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = WisepennyColors.TextPrimary,
            unfocusedTextColor = WisepennyColors.TextPrimary,
            focusedContainerColor = WisepennyColors.SurfaceElevated,
            unfocusedContainerColor = WisepennyColors.SurfaceElevated,
            focusedBorderColor = WisepennyColors.AccentMint,
            unfocusedBorderColor = WisepennyColors.BorderSubtle,
            cursorColor = WisepennyColors.AccentMint,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

// ---------------------------------------------------------------------------
// Money formatting
// ---------------------------------------------------------------------------

private fun currencySymbol(code: String): String = when (code) {
    "USD", "CAD" -> "$"
    "GBP" -> "£"
    "CHF" -> "CHF"
    else -> "€"
}

/** French thousands grouping with the currency symbol — e.g. 300000 -> "3 000 €". */
private fun formatMoney(cents: Long, currency: String): String {
    val units = cents / 100
    val grouped = units.toString()
        .reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()
    return "$grouped ${currencySymbol(currency)}"
}

@Preview
@Composable
private fun OnboardingAmountPreview() {
    WisepennyTheme {
        OnboardingScreen(
            state = OnboardingUiState(
                today = LocalDate(2026, 9, 9),
                step = OnboardingStep.AMOUNT,
                category = OnboardingCategory.ALL.first(),
                amountUnits = 3000,
            ),
            onCurrencySelect = {},
            onMotivationSelect = {},
            onFirstNameChange = {},
            onCategorySelect = {},
            onAmountIncrement = {},
            onAmountDecrement = {},
            onAmountPreset = {},
            onBankSelect = {},
            onBack = {},
            onNext = {},
            onSkip = {},
        )
    }
}
