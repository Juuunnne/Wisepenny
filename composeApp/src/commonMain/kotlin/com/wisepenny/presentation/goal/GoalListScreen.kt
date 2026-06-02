package com.wisepenny.presentation.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisepenny.presentation.components.SavingsProgressBar
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyTheme

data class GoalListUiState(
    val goals: List<GoalListItem>,
)

data class GoalListItem(
    val id: Long,
    val iconKey: String,
    val name: String,
    val savedLabel: String,
    val targetLabel: String,
    val progress: Float,
    val percentLabel: String,
    val remainingLabel: String,
    val isPriority: Boolean,
)

/** Shared icon mapping for the goal screens — keeps the mockup's pictograms simple. */
internal fun goalEmoji(iconKey: String): String = when (iconKey) {
    "travel", "plane" -> "✈️"
    "laptop", "tech" -> "💻"
    "home" -> "🏠"
    "car" -> "🚗"
    "gift" -> "🎁"
    "coffee" -> "☕"
    "shopping" -> "🛍️"
    else -> "🎯"
}

@Composable
fun GoalListScreen(
    state: GoalListUiState,
    onGoalClick: (Long) -> Unit,
    onAddGoal: () -> Unit,
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl)
                .padding(top = Spacing.xl, bottom = Spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Text(
                text = "Tes Objectifs",
                style = MaterialTheme.typography.headlineMedium,
                color = WisepennyColors.TextPrimary,
            )
            state.goals.forEach { goal ->
                GoalCard(goal = goal, onClick = { onGoalClick(goal.id) })
            }
            OutlinedButton(
                onClick = onAddGoal,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Spacing.sm),
            ) {
                Text(
                    text = "+ Ajouter un objectif",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WisepennyColors.TextPrimary,
                )
            }
        }
    }
}

@Composable
private fun GoalCard(goal: GoalListItem, onClick: () -> Unit) {
    val onCard = if (goal.isPriority) WisepennyColors.TextOnLight else WisepennyColors.TextPrimary
    val mutedOnCard =
        if (goal.isPriority) WisepennyColors.TextOnLightMuted else WisepennyColors.TextTertiary
    val container =
        if (goal.isPriority) WisepennyColors.SurfaceLight else WisepennyColors.SurfaceElevated
    val track =
        if (goal.isPriority) WisepennyColors.SurfaceLightAlt else WisepennyColors.BorderSubtle

    Card(
        colors = CardDefaults.cardColors(containerColor = container),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                GoalIcon(iconKey = goal.iconKey, isPriority = goal.isPriority)
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = onCard,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                if (goal.isPriority) {
                    PriorityPill()
                }
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = goal.savedLabel,
                    style = MaterialTheme.typography.displayMedium,
                    color = onCard,
                )
                Text(
                    text = " ${goal.targetLabel}",
                    style = MaterialTheme.typography.titleMedium,
                    color = mutedOnCard,
                    modifier = Modifier.padding(bottom = Spacing.xs),
                )
            }
            SavingsProgressBar(progress = goal.progress, trackColor = track)
            if (goal.isPriority) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = goal.percentLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = mutedOnCard,
                    )
                    Text(
                        text = goal.remainingLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = mutedOnCard,
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalIcon(iconKey: String, isPriority: Boolean) {
    val bg = if (isPriority) WisepennyColors.AccentMintSoft else WisepennyColors.BackgroundPrimary
    Box(
        modifier = Modifier.size(40.dp).clip(CircleShape).background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = goalEmoji(iconKey), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun PriorityPill() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, WisepennyColors.TextOnLightMuted, RoundedCornerShape(50))
            .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
    ) {
        Text(
            text = "Priorité",
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.TextOnLightMuted,
        )
    }
}

@Preview
@Composable
private fun GoalListScreenPreview() {
    WisepennyTheme {
        GoalListScreen(
            state = GoalListUiState(
                goals = listOf(
                    GoalListItem(
                        id = 1,
                        iconKey = "travel",
                        name = "Voyage au Japon",
                        savedLabel = "1 250 €",
                        targetLabel = "/ 3 000 €",
                        progress = 0.41f,
                        percentLabel = "41% complété",
                        remainingLabel = "Il reste 1 750 €",
                        isPriority = true,
                    ),
                    GoalListItem(
                        id = 2,
                        iconKey = "laptop",
                        name = "Nouvel ordi",
                        savedLabel = "450 €",
                        targetLabel = "/ 1 200 €",
                        progress = 0.37f,
                        percentLabel = "37% complété",
                        remainingLabel = "Il reste 750 €",
                        isPriority = false,
                    ),
                ),
            ),
            onGoalClick = {},
            onAddGoal = {},
        )
    }
}
