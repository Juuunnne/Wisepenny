package com.wisepenny

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wisepenny.presentation.challenge.ChallengeDetailScreen
import com.wisepenny.presentation.challenge.ChallengeViewModel
import com.wisepenny.presentation.goal.GoalDetailScreen
import com.wisepenny.presentation.goal.GoalListScreen
import com.wisepenny.presentation.goal.GoalViewModel
import com.wisepenny.presentation.theme.WisepennyTheme
import org.koin.compose.viewmodel.koinViewModel

// TEMPORARY in-memory routes. Step 6 replaces this whole block with a real
// navigation graph + the bottom navigation bar shown in the mockups.
private sealed interface Route {
    data object GoalList : Route
    data class GoalDetail(val goalId: Long) : Route
    data object Challenge : Route
}

@Composable
fun App() {
    WisepennyTheme {
        var route by remember { mutableStateOf<Route>(Route.GoalList) }
        var lastGoalId by remember { mutableStateOf<Long?>(null) }

        val goalViewModel = koinViewModel<GoalViewModel>()

        when (route) {
            is Route.GoalList -> {
                val state by goalViewModel.listState.collectAsStateWithLifecycle()
                GoalListScreen(
                    state = state,
                    onGoalClick = { id ->
                        goalViewModel.selectGoal(id)
                        lastGoalId = id
                        route = Route.GoalDetail(id)
                    },
                    onAddGoal = { /* TODO: a create-goal form lands in a later step */ },
                )
            }

            is Route.GoalDetail -> {
                val detail by goalViewModel.detailState.collectAsStateWithLifecycle()
                detail?.let { state ->
                    GoalDetailScreen(
                        state = state,
                        onBack = {
                            goalViewModel.clearSelection()
                            route = Route.GoalList
                        },
                        onEdit = { /* TODO: Step 6+ edit goal */ },
                        onQuickAdd = goalViewModel::onQuickAdd,
                        onAddManual = goalViewModel::onAddManual,
                        onSetAutoSave = goalViewModel::onSetAutoSave,
                        // Temporary jump to the Challenge screen so the linked
                        // challenge → goal loop is testable before Step 6 nav.
                        onChallengeClick = { route = Route.Challenge },
                        onSeeAllChallenges = { route = Route.Challenge },
                    )
                }
            }

            is Route.Challenge -> {
                val challengeViewModel = koinViewModel<ChallengeViewModel>()
                val state by challengeViewModel.uiState.collectAsStateWithLifecycle()
                ChallengeDetailScreen(
                    state = state,
                    onValidateDay = challengeViewModel::onValidateDay,
                    onSkipDay = challengeViewModel::onSkipDay,
                    onBack = {
                        route = lastGoalId?.let { Route.GoalDetail(it) } ?: Route.GoalList
                    },
                    onShare = { /* TODO: Step 9 will wire this to native share sheet */ },
                )
            }
        }
    }
}
