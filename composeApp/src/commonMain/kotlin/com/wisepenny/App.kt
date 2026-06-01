package com.wisepenny

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisepenny.presentation.challenge.ChallengeDetailScreen
import com.wisepenny.presentation.challenge.ChallengeViewModel
import com.wisepenny.presentation.theme.WisepennyTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    WisepennyTheme {
        val viewModel = koinViewModel<ChallengeViewModel>()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        ChallengeDetailScreen(
            state = state,
            onValidateDay = viewModel::onValidateDay,
            onSkipDay = viewModel::onSkipDay,
            onBack = { /* TODO: Step 6 will wire this to navigation */ },
            onShare = { /* TODO: Step 9 will wire this to native share sheet */ },
        )
    }
}
