package com.wisepenny.presentation.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisepenny.domain.model.SavingsCadence
import com.wisepenny.presentation.components.SavingsProgressBar
import com.wisepenny.presentation.components.WisepennyCard
import com.wisepenny.presentation.components.WisepennyCardVariant
import com.wisepenny.presentation.components.WisepennyTopBar
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyShapes
import com.wisepenny.presentation.theme.WisepennyTheme
import kotlin.math.roundToInt

data class GoalDetailUiState(
    val id: Long,
    val category: String,
    val name: String,
    val subtitle: String,
    val savedLabel: String,
    val targetLabel: String,
    val progress: Float,
    val percentLabel: String,
    val remainingLabel: String,
    val projectionMessage: String,
    val projectionDate: String?,
    val projectionBars: List<Float>,
    val linkedChallenges: List<LinkedChallengeItem>,
)

data class LinkedChallengeItem(
    val id: Long,
    val iconKey: String,
    val title: String,
    val contributionLabel: String,
    val statusLabel: String?,
    val isStarted: Boolean,
)

@Composable
fun GoalDetailScreen(
    state: GoalDetailUiState,
    onBack: () -> Unit,
    onAddLinkedChallenge: () -> Unit,
    onAddManual: (Long) -> Unit,
    onSetAutoSave: (Long, SavingsCadence) -> Unit,
    onChallengeClick: (Long) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showAutoSaveDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            WisepennyTopBar(title = "Objectif", onBack = onBack)
        },
        containerColor = WisepennyColors.BackgroundPrimary,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            GoalHeader(category = state.category, name = state.name, subtitle = state.subtitle)
            GoalHeroCard(state = state)
            ActionCardsRow(
                onAdd = { showAddDialog = true },
                onProgram = { showAutoSaveDialog = true },
            )
            ProjectionSection(
                message = state.projectionMessage,
                date = state.projectionDate,
                bars = state.projectionBars,
                progress = state.progress,
            )
            LinkedChallengesSection(
                challenges = state.linkedChallenges,
                onChallengeClick = onChallengeClick,
            )
            AddLinkedChallengeButton(onClick = onAddLinkedChallenge)
        }
    }

    if (showAddDialog) {
        AmountDialog(
            title = "Ajouter à l'objectif",
            confirmLabel = "Ajouter",
            onConfirm = { cents ->
                onAddManual(cents)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }
    if (showAutoSaveDialog) {
        AutoSaveDialog(
            onConfirm = { cents, cadence ->
                onSetAutoSave(cents, cadence)
                showAutoSaveDialog = false
            },
            onDismiss = { showAutoSaveDialog = false },
        )
    }
}

@Composable
private fun GoalHeader(category: String, name: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
        Text(
            text = category.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.AccentMint,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = name,
            style = MaterialTheme.typography.displayMedium,
            color = WisepennyColors.TextPrimary,
        )
        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextTertiary,
            )
        }
    }
}

@Composable
private fun GoalHeroCard(state: GoalDetailUiState) {
    WisepennyCard(
        variant = WisepennyCardVariant.Light,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "ÉPARGNÉ",
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.TextOnLightMuted,
        )
        Text(
            text = state.savedLabel,
            style = MaterialTheme.typography.displayLarge,
            color = WisepennyColors.TextOnLight,
        )
        Text(
            text = state.targetLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = WisepennyColors.TextOnLightMuted,
        )
        SavingsProgressBar(
            progress = state.progress,
            trackColor = WisepennyColors.SurfaceLightAlt,
            modifier = Modifier.padding(top = Spacing.xs),
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.xxs),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = state.percentLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextOnLight,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = state.remainingLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextOnLight,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ActionCardsRow(onAdd: () -> Unit, onProgram: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        ActionCard(
            icon = { Icon(Icons.Filled.Add, contentDescription = null, tint = WisepennyColors.AccentMint) },
            title = "Ajouter",
            subtitle = "manuellement",
            onClick = onAdd,
            modifier = Modifier.weight(1f),
        )
        ActionCard(
            icon = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = WisepennyColors.AccentMint) },
            title = "Programmer",
            subtitle = "auto-épargne",
            onClick = onProgram,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ActionCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WisepennyCard(
        variant = WisepennyCardVariant.Elevated,
        shape = WisepennyShapes.medium,
        onClick = onClick,
        contentPadding = PaddingValues(Spacing.lg),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(WisepennyColors.BackgroundPrimary),
            contentAlignment = Alignment.Center,
        ) { icon() }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = WisepennyColors.TextPrimary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.TextTertiary,
        )
    }
}

@Composable
private fun ProjectionSection(message: String, date: String?, bars: List<Float>, progress: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        SectionHeader(title = "Projection")
        WisepennyCard(
            variant = WisepennyCardVariant.Elevated,
            shape = WisepennyShapes.medium,
            contentPadding = PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = buildAnnotatedString {
                    append(message)
                    if (date != null) {
                        append(" ")
                        withStyle(SpanStyle(color = WisepennyColors.AccentMint, fontWeight = FontWeight.Bold)) {
                            append(date)
                        }
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextSecondary,
            )
            if (bars.isNotEmpty()) {
                val currentIndex = (progress.coerceIn(0f, 1f) * (bars.size - 1)).roundToInt()
                ProjectionChart(bars = bars, currentIndex = currentIndex)
            }
        }
    }
}

@Composable
private fun ProjectionChart(bars: List<Float>, currentIndex: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        bars.forEachIndexed { index, fraction ->
            val color =
                if (index == currentIndex) WisepennyColors.AccentMint else WisepennyColors.BorderSubtle
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height((80 * fraction.coerceIn(0.08f, 1f)).dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color),
            )
        }
    }
}

@Composable
private fun LinkedChallengesSection(
    challenges: List<LinkedChallengeItem>,
    onChallengeClick: (Long) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text(
            text = "Défis liés",
            style = MaterialTheme.typography.headlineMedium,
            color = WisepennyColors.TextPrimary,
        )
        if (challenges.isEmpty()) {
            Text(
                text = "Aucun défi lié pour l'instant",
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextTertiary,
            )
        } else {
            challenges.forEach { challenge ->
                LinkedChallengeRow(challenge = challenge, onClick = { onChallengeClick(challenge.id) })
            }
        }
    }
}

@Composable
private fun LinkedChallengeRow(challenge: LinkedChallengeItem, onClick: () -> Unit) {
    val onCard = if (challenge.isStarted) WisepennyColors.TextOnLight else WisepennyColors.TextPrimary
    val muted =
        if (challenge.isStarted) WisepennyColors.TextOnLightMuted else WisepennyColors.TextTertiary
    val iconBg =
        if (challenge.isStarted) WisepennyColors.SurfaceLightAlt else WisepennyColors.BackgroundPrimary

    WisepennyCard(
        variant = if (challenge.isStarted) WisepennyCardVariant.Light else WisepennyCardVariant.Elevated,
        shape = WisepennyShapes.medium,
        onClick = onClick,
        contentPadding = PaddingValues(Spacing.md),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = goalEmoji(challenge.iconKey), style = MaterialTheme.typography.bodyMedium)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = onCard,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = challenge.contributionLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = muted,
                )
            }
            if (challenge.isStarted && challenge.statusLabel != null) {
                StatusPill(text = challenge.statusLabel)
            } else {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WisepennyColors.SurfaceLight,
                        contentColor = WisepennyColors.TextOnLight,
                    ),
                    shape = WisepennyShapes.extraSmall,
                ) {
                    Text(text = "Démarrer", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun StatusPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(WisepennyColors.AccentMintSoft)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.TextOnLight,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AddLinkedChallengeButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = WisepennyColors.AccentMint,
            contentColor = WisepennyColors.TextOnLight,
        ),
        shape = WisepennyShapes.small,
        modifier = Modifier.fillMaxWidth().height(56.dp),
    ) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
        Text(
            text = "Ajouter un défi lié",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = Spacing.xs),
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = WisepennyColors.TextPrimary,
    )
}

@Preview
@Composable
private fun GoalDetailScreenPreview() {
    WisepennyTheme {
        GoalDetailScreen(
            state = GoalDetailUiState(
                id = 1,
                category = "Voyage",
                name = "Lisbonne 2026",
                subtitle = "Avec Sarah et Tom",
                savedLabel = "450 €",
                targetLabel = "sur 800 €",
                progress = 0.56f,
                percentLabel = "56 % Atteint",
                remainingLabel = "350 € Restant",
                projectionMessage = "Tu atteindras ton objectif le",
                projectionDate = "15 mars 2026",
                projectionBars = listOf(0.2f, 0.35f, 0.5f, 0.7f, 0.85f, 1f),
                linkedChallenges = listOf(
                    LinkedChallengeItem(1, "coffee", "7 jours sans café", "+15 € vers Lisbonne", "Jour 5/7", true),
                    LinkedChallengeItem(2, "shopping", "Pas d'achat impulsif", "+20 € vers Lisbonne", null, false),
                ),
            ),
            onBack = {},
            onAddLinkedChallenge = {},
            onAddManual = {},
            onSetAutoSave = { _, _ -> },
            onChallengeClick = {},
        )
    }
}
