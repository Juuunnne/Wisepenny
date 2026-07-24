package com.wisepenny.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisepenny.presentation.components.SavingsProgressBar
import com.wisepenny.presentation.components.WisepennyCard
import com.wisepenny.presentation.components.WisepennyCardVariant
import com.wisepenny.presentation.components.WisepennyScaffold
import com.wisepenny.presentation.components.WisepennyScreenHeader
import com.wisepenny.presentation.goal.goalEmoji
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyShapes
import com.wisepenny.presentation.theme.WisepennyTheme

data class DashboardUiState(
    val greetingName: String,
    val avatarInitials: String,
    val savedThisMonthLabel: String,
    val monthlyDeltaLabel: String?,
    val monthlyObjectiveLabel: String,
    val monthlyProgress: Float,
    val streakDays: Int,
    val streakTotal: Int,
    val goals: List<DashboardGoalItem>,
    val activeChallenges: List<DashboardChallengeItem>,
    val showDailyChallenge: Boolean,
)

data class DashboardGoalItem(
    val id: Long,
    val iconKey: String,
    val name: String,
    val savedLabel: String,
    val targetLabel: String,
    val progress: Float,
    val isPriority: Boolean,
)

data class DashboardChallengeItem(
    val id: Long,
    val iconKey: String,
    val title: String,
    val dayLabel: String,
    val percentLabel: String,
)

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onGoalClick: (Long) -> Unit,
    onChallengeClick: (Long) -> Unit,
    onAcceptChallenge: () -> Unit,
) {
    WisepennyScaffold(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        WisepennyScreenHeader(
            title = "Bonjour ${state.greetingName}".trimEnd(),
            leading = {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(WisepennyColors.AccentMint),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.avatarInitials,
                        style = MaterialTheme.typography.labelSmall,
                        color = WisepennyColors.TextOnLight,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
        )
        MonthlySavingsCard(state = state)
        StreakCard(days = state.streakDays, total = state.streakTotal)
        GoalsSection(goals = state.goals, onGoalClick = onGoalClick)
        ChallengesSection(challenges = state.activeChallenges, onChallengeClick = onChallengeClick)
        if (state.showDailyChallenge) {
            PromoCard(onAccept = onAcceptChallenge)
        }
    }
}

@Composable
private fun MonthlySavingsCard(state: DashboardUiState) {
    WisepennyCard(
        variant = WisepennyCardVariant.Light,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "ÉPARGNE DU MOIS",
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.TextOnLightMuted,
        )
        Text(
            text = state.savedThisMonthLabel,
            style = MaterialTheme.typography.displayLarge,
            color = WisepennyColors.TextOnLight,
        )
        if (state.monthlyDeltaLabel != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(WisepennyColors.AccentMint)
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
            ) {
                Text(
                    text = "↗ ${state.monthlyDeltaLabel}",
                    style = MaterialTheme.typography.labelSmall,
                    color = WisepennyColors.TextOnLight,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        SavingsProgressBar(
            progress = state.monthlyProgress,
            trackColor = WisepennyColors.SurfaceLightAlt,
            modifier = Modifier.padding(top = Spacing.xs),
        )
        Text(
            text = state.monthlyObjectiveLabel,
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.TextOnLightMuted,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun StreakCard(days: Int, total: Int) {
    WisepennyCard(
        variant = WisepennyCardVariant.Elevated,
        shape = WisepennyShapes.medium,
        contentPadding = PaddingValues(Spacing.lg),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(
                    text = "$days jours d'affilée",
                    style = MaterialTheme.typography.titleMedium,
                    color = WisepennyColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "🔥 Connexion réussie",
                    style = MaterialTheme.typography.labelSmall,
                    color = WisepennyColors.TextTertiary,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                for (i in 1..total) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (i <= days) WisepennyColors.AccentMint else WisepennyColors.BorderSubtle,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalsSection(goals: List<DashboardGoalItem>, onGoalClick: (Long) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text(
            text = "Mes objectifs",
            style = MaterialTheme.typography.headlineMedium,
            color = WisepennyColors.TextPrimary,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            items(goals) { goal ->
                DashboardGoalCard(goal = goal, onClick = { onGoalClick(goal.id) })
            }
        }
    }
}

@Composable
private fun DashboardGoalCard(goal: DashboardGoalItem, onClick: () -> Unit) {
    val onCard = if (goal.isPriority) WisepennyColors.TextOnLight else WisepennyColors.TextPrimary
    val muted =
        if (goal.isPriority) WisepennyColors.TextOnLightMuted else WisepennyColors.TextTertiary
    val track =
        if (goal.isPriority) WisepennyColors.SurfaceLightAlt else WisepennyColors.BorderSubtle

    WisepennyCard(
        variant = if (goal.isPriority) WisepennyCardVariant.Light else WisepennyCardVariant.Elevated,
        shape = WisepennyShapes.medium,
        onClick = onClick,
        contentPadding = PaddingValues(Spacing.lg),
        modifier = Modifier.width(180.dp),
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(
                if (goal.isPriority) WisepennyColors.AccentMintSoft else WisepennyColors.BackgroundPrimary,
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = goalEmoji(goal.iconKey), style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            text = goal.name,
            style = MaterialTheme.typography.bodyMedium,
            color = onCard,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = goal.savedLabel,
                style = MaterialTheme.typography.titleMedium,
                color = onCard,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = " / ${goal.targetLabel}",
                style = MaterialTheme.typography.labelSmall,
                color = muted,
                modifier = Modifier.padding(bottom = Spacing.xxs),
            )
        }
        SavingsProgressBar(progress = goal.progress, trackColor = track)
    }
}

@Composable
private fun ChallengesSection(
    challenges: List<DashboardChallengeItem>,
    onChallengeClick: (Long) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text(
            text = "Défis actifs",
            style = MaterialTheme.typography.headlineMedium,
            color = WisepennyColors.TextPrimary,
        )
        if (challenges.isEmpty()) {
            Text(
                text = "Aucun défi actif",
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextTertiary,
            )
        } else {
            challenges.forEach { challenge ->
                DashboardChallengeRow(challenge = challenge, onClick = { onChallengeClick(challenge.id) })
            }
        }
    }
}

@Composable
private fun DashboardChallengeRow(challenge: DashboardChallengeItem, onClick: () -> Unit) {
    WisepennyCard(
        variant = WisepennyCardVariant.Elevated,
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
                modifier = Modifier.size(40.dp).clip(CircleShape).background(WisepennyColors.BackgroundPrimary),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = goalEmoji(challenge.iconKey), style = MaterialTheme.typography.bodyMedium)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WisepennyColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = challenge.dayLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = WisepennyColors.TextTertiary,
                )
            }
            Text(
                text = challenge.percentLabel,
                style = MaterialTheme.typography.titleMedium,
                color = WisepennyColors.AccentMint,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun PromoCard(onAccept: () -> Unit) {
    WisepennyCard(
        variant = WisepennyCardVariant.Accent,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(WisepennyColors.BackgroundPrimary),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "☕", style = MaterialTheme.typography.titleMedium)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(WisepennyColors.SurfaceLight)
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
            ) {
                Text(
                    text = "NOUVEAU",
                    style = MaterialTheme.typography.labelSmall,
                    color = WisepennyColors.TextOnLight,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Text(
            text = "Économise le prix d'un café",
            style = MaterialTheme.typography.headlineMedium,
            color = WisepennyColors.TextOnLight,
        )
        Text(
            text = "Mets 3 € de côté aujourd'hui au lieu de prendre ton café habituel en terrasse.",
            style = MaterialTheme.typography.bodyMedium,
            color = WisepennyColors.TextOnLight,
        )
        Button(
            onClick = onAccept,
            colors = ButtonDefaults.buttonColors(
                containerColor = WisepennyColors.BackgroundPrimary,
                contentColor = WisepennyColors.AccentMint,
            ),
            shape = WisepennyShapes.small,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Text(
                text = "Relever le défi ↗",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview
@Composable
private fun DashboardScreenPreview() {
    WisepennyTheme {
        DashboardScreen(
            state = DashboardUiState(
                greetingName = "Alex",
                avatarInitials = "A",
                savedThisMonthLabel = "127 €",
                monthlyDeltaLabel = "+18% vs mois dernier",
                monthlyObjectiveLabel = "Objectif mensuel : 200 €",
                monthlyProgress = 0.63f,
                streakDays = 5,
                streakTotal = 7,
                goals = listOf(
                    DashboardGoalItem(1, "travel", "Voyage à Lisbonne", "450 €", "800 €", 0.56f, true),
                    DashboardGoalItem(2, "laptop", "MacBook", "320 €", "1 200 €", 0.27f, false),
                ),
                activeChallenges = listOf(
                    DashboardChallengeItem(1, "coffee", "7 jours sans café", "Jour 5 sur 7", "71%"),
                    DashboardChallengeItem(2, "shopping", "Pas d'achats impulsifs", "Jour 2 sur 7", "28%"),
                ),
                showDailyChallenge = false,
            ),
            onGoalClick = {},
            onChallengeClick = {},
            onAcceptChallenge = {},
        )
    }
}
