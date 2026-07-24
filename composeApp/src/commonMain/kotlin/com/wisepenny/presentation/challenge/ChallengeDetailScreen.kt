package com.wisepenny.presentation.challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisepenny.presentation.components.WisepennyCard
import com.wisepenny.presentation.components.WisepennyCardVariant
import com.wisepenny.presentation.components.WisepennyTopBar
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors
import com.wisepenny.presentation.theme.WisepennyShapes
import com.wisepenny.presentation.theme.WisepennyTheme

data class ChallengeUiState(
    val title: String,
    val subtitle: String,
    val totalSavings: String,
    val xpGained: String,
    val completedDays: Int,
    val currentDay: Int,
    val totalDays: Int,
    val todayQuestion: String,
    val todayActionLabel: String,
    val history: List<HistoryDay>,
)

data class HistoryDay(
    val dayLabel: String,
    val amount: String,
)

@Composable
fun ChallengeDetailScreen(
    state: ChallengeUiState,
    onValidateDay: () -> Unit,
    onSkipDay: () -> Unit,
    onBack: () -> Unit,
    onShare: () -> Unit,
) {
    Scaffold(
        topBar = {
            WisepennyTopBar(
                title = "Mon défi",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Outlined.IosShare,
                            contentDescription = "Partager",
                            tint = WisepennyColors.TextPrimary,
                        )
                    }
                },
            )
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
            HeroCard(
                title = state.title,
                subtitle = state.subtitle,
                completedDays = state.completedDays,
                currentDay = state.currentDay,
                totalDays = state.totalDays,
            )
            StatsStrip(
                totalSavings = state.totalSavings,
                completedDays = state.completedDays,
                totalDays = state.totalDays,
                xpGained = state.xpGained,
            )
            TodaySection(
                currentDay = state.currentDay,
                question = state.todayQuestion,
                actionLabel = state.todayActionLabel,
                onValidateDay = onValidateDay,
                onSkipDay = onSkipDay,
            )
            HistorySection(history = state.history)
        }
    }
}

@Composable
private fun HeroCard(
    title: String,
    subtitle: String,
    completedDays: Int,
    currentDay: Int,
    totalDays: Int,
) {
    WisepennyCard(
        variant = WisepennyCardVariant.Accent,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "DÉFI EN COURS",
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.TextOnLightMuted,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.displayMedium,
            color = WisepennyColors.TextOnLight,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = WisepennyColors.TextOnLight,
        )
        DayProgressDots(
            completedDays = completedDays,
            currentDay = currentDay,
            totalDays = totalDays,
        )
    }
}

private enum class DayState { Completed, Current, Future }

@Composable
private fun DayProgressDots(completedDays: Int, currentDay: Int, totalDays: Int) {
    Row(
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription =
                "Progression du défi : jour $currentDay sur $totalDays, $completedDays jours complétés"
        },
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        for (day in 1..totalDays) {
            DayDot(
                day = day,
                state = when {
                    day <= completedDays -> DayState.Completed
                    day == currentDay -> DayState.Current
                    else -> DayState.Future
                },
            )
        }
    }
}

@Composable
private fun DayDot(day: Int, state: DayState) {
    val dotSize = 32.dp
    when (state) {
        DayState.Completed -> Box(
            modifier = Modifier.size(dotSize).clip(CircleShape).background(WisepennyColors.BackgroundPrimary),
            contentAlignment = Alignment.Center,
        ) {
            Text("✓", color = WisepennyColors.TextPrimary, style = MaterialTheme.typography.bodyMedium)
        }
        DayState.Current -> Box(
            modifier = Modifier.size(dotSize).clip(CircleShape).border(2.dp, WisepennyColors.TextPrimary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("$day", color = WisepennyColors.TextPrimary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        DayState.Future -> Box(
            modifier = Modifier.size(dotSize).clip(CircleShape).border(1.dp, WisepennyColors.TextOnLightMuted, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("$day", color = WisepennyColors.TextOnLightMuted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun StatsStrip(
    totalSavings: String,
    completedDays: Int,
    totalDays: Int,
    xpGained: String,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        StatCard(value = totalSavings, label = "Économisé", modifier = Modifier.weight(1f))
        StatCard(value = "$completedDays / $totalDays", label = "Jours", modifier = Modifier.weight(1f))
        StatCard(
            value = xpGained,
            label = "Gagnés",
            modifier = Modifier.weight(1f),
            valueColor = WisepennyColors.AccentMint,
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = WisepennyColors.TextPrimary,
) {
    WisepennyCard(
        variant = WisepennyCardVariant.Elevated,
        shape = WisepennyShapes.small,
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = WisepennyColors.TextTertiary,
        )
    }
}

@Composable
private fun TodaySection(
    currentDay: Int,
    question: String,
    actionLabel: String,
    onValidateDay: () -> Unit,
    onSkipDay: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        SectionHeader(title = "Aujourd'hui", trailing = "Jour $currentDay")
        TodayActionCard(
            question = question,
            actionLabel = actionLabel,
            onValidate = onValidateDay,
            onSkip = onSkipDay,
        )
    }
}

@Composable
private fun SectionHeader(title: String, trailing: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = WisepennyColors.TextPrimary,
        )
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelSmall,
                color = WisepennyColors.TextTertiary,
            )
        }
    }
}

@Composable
private fun TodayActionCard(
    question: String,
    actionLabel: String,
    onValidate: () -> Unit,
    onSkip: () -> Unit,
) {
    WisepennyCard(
        variant = WisepennyCardVariant.Light,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(WisepennyColors.AccentMintSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text("☕", style = MaterialTheme.typography.titleMedium)
        }
        Text(
            text = question,
            style = MaterialTheme.typography.titleMedium,
            color = WisepennyColors.TextOnLight,
            fontWeight = FontWeight.Bold,
        )
        Button(
            onClick = onValidate,
            colors = ButtonDefaults.buttonColors(
                containerColor = WisepennyColors.AccentMint,
                contentColor = WisepennyColors.TextOnLight,
            ),
            shape = WisepennyShapes.small,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        OutlinedButton(
            onClick = onSkip,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = WisepennyColors.TextOnLight,
            ),
            shape = WisepennyShapes.small,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Text(
                text = "Pas aujourd'hui",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun HistorySection(history: List<HistoryDay>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        SectionHeader(title = "Historique")
        HistoryList(history = history)
    }
}

@Composable
private fun HistoryList(history: List<HistoryDay>) {
    WisepennyCard(
        variant = WisepennyCardVariant.Elevated,
        shape = WisepennyShapes.medium,
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth(),
    ) {
        history.forEachIndexed { index, day ->
            HistoryRow(day = day.dayLabel, amount = day.amount)
            if (index < history.lastIndex) {
                HorizontalDivider(
                    color = WisepennyColors.BorderSubtle,
                    modifier = Modifier.padding(horizontal = Spacing.md),
                )
            }
        }
    }
}

@Composable
private fun HistoryRow(day: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.bodyMedium,
            color = WisepennyColors.TextPrimary,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = amount,
                style = MaterialTheme.typography.bodyMedium,
                color = WisepennyColors.TextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(WisepennyColors.Success),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "✓",
                    color = WisepennyColors.TextPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChallengeDetailScreenPreview() {
    WisepennyTheme {
        ChallengeDetailScreen(
            state = ChallengeUiState(
                title = "7 jours sans café",
                subtitle = "Économise 21 € en une semaine",
                totalSavings = "15 €",
                xpGained = "+25 XP",
                completedDays = 5,
                currentDay = 6,
                totalDays = 7,
                todayQuestion = "As-tu sauté ton café ce matin?",
                todayActionLabel = "Oui, +3 € épargnés",
                history = listOf(
                    HistoryDay("Lundi", "+3 €"),
                    HistoryDay("Mardi", "+3 €"),
                    HistoryDay("Mercredi", "+3 €"),
                    HistoryDay("Jeudi", "+3 €"),
                    HistoryDay("Vendredi", "+3 €"),
                ),
            ),
            onValidateDay = {},
            onSkipDay = {},
            onBack = {},
            onShare = {},
        )
    }
}
