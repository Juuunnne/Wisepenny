package com.wisepenny

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wisepenny.presentation.challenge.ChallengeDetailScreen
import com.wisepenny.presentation.challenge.ChallengeUiState
import com.wisepenny.presentation.challenge.HistoryDay
import com.wisepenny.presentation.theme.WisepennyTheme

@Composable
@Preview
fun App() {
    WisepennyTheme {
        ChallengeDetailScreen(
            state = sampleChallengeUiState(),
            onValidateDay = { /* TODO: Step 4 will wire this to the ViewModel */ },
            onSkipDay = { /* TODO: Step 4 will wire this to the ViewModel */ },
            onBack = { /* TODO: Step 6 will wire this to navigation */ },
            onShare = { /* TODO: Step 9 will wire this to native share sheet */ },
        )
    }
}

private fun sampleChallengeUiState(): ChallengeUiState {
    val completedDays = 2
    val totalDays = 7
    val dailyAmountEuros = 3
    val dayLabels = listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche")
    return ChallengeUiState(
        title = "7 jours sans café",
        subtitle = "Économise 21 € en une semaine",
        totalSavings = "${completedDays * dailyAmountEuros} €",
        xpGained = "+25 XP",
        completedDays = completedDays,
        currentDay = completedDays + 1,
        totalDays = totalDays,
        todayQuestion = "As-tu sauté ton café ce matin?",
        todayActionLabel = "Oui, $dailyAmountEuros € épargnés",
        history = dayLabels.take(completedDays).map { HistoryDay(it, "+$dailyAmountEuros €") },
    )
}
