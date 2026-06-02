package com.wisepenny.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.wisepenny.presentation.challenge.ChallengeDetailScreen
import com.wisepenny.presentation.challenge.ChallengeViewModel
import com.wisepenny.presentation.common.ComingSoonScreen
import com.wisepenny.presentation.dashboard.DashboardScreen
import com.wisepenny.presentation.dashboard.DashboardViewModel
import com.wisepenny.presentation.goal.GoalDetailScreen
import com.wisepenny.presentation.goal.GoalListScreen
import com.wisepenny.presentation.goal.GoalViewModel
import com.wisepenny.presentation.theme.WisepennyColors
import org.koin.compose.viewmodel.koinViewModel

/**
 * The app's real navigation graph: a 4-tab bottom bar over the tab destinations,
 * with detail screens (goal, challenge) layered full-screen above. Replaces the
 * temporary in-memory router that hosted the screens before Step 6.
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBar = TAB_ROUTES.any { route ->
        currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WisepennyColors.BackgroundPrimary),
    ) {
        NavHost(
            navController = navController,
            startDestination = Accueil,
            modifier = Modifier.weight(1f),
        ) {
            composable<Accueil> {
                val viewModel = koinViewModel<DashboardViewModel>()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                DashboardScreen(
                    state = state,
                    onGoalClick = { navController.navigate(GoalDetailRoute(it)) },
                    onChallengeClick = { navController.navigate(ChallengeDetailRoute(it)) },
                )
            }

            composable<Apprendre> { ComingSoonScreen(title = "Apprendre") }

            composable<Objectifs> {
                val viewModel = koinViewModel<GoalViewModel>()
                val state by viewModel.listState.collectAsStateWithLifecycle()
                GoalListScreen(
                    state = state,
                    onGoalClick = { navController.navigate(GoalDetailRoute(it)) },
                    onAddGoal = { /* TODO: a create-goal form lands in a later step */ },
                )
            }

            composable<Profil> { ComingSoonScreen(title = "Profil") }

            composable<GoalDetailRoute> { entry ->
                val goalId = entry.toRoute<GoalDetailRoute>().goalId
                val viewModel = koinViewModel<GoalViewModel>()
                LaunchedEffect(goalId) { viewModel.selectGoal(goalId) }
                val detail by viewModel.detailState.collectAsStateWithLifecycle()
                detail?.let { state ->
                    GoalDetailScreen(
                        state = state,
                        onBack = { navController.popBackStack() },
                        onEdit = { /* TODO: edit goal */ },
                        onQuickAdd = viewModel::onQuickAdd,
                        onAddManual = viewModel::onAddManual,
                        onSetAutoSave = viewModel::onSetAutoSave,
                        onChallengeClick = { navController.navigate(ChallengeDetailRoute(it)) },
                        onSeeAllChallenges = { /* TODO: a challenge list lands in a later step */ },
                    )
                }
            }

            composable<ChallengeDetailRoute> {
                // The Challenge screen currently shows the single active challenge;
                // viewing an arbitrary challenge by id is a later refinement.
                val viewModel = koinViewModel<ChallengeViewModel>()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                ChallengeDetailScreen(
                    state = state,
                    onValidateDay = viewModel::onValidateDay,
                    onSkipDay = viewModel::onSkipDay,
                    onBack = { navController.popBackStack() },
                    onShare = { /* TODO: Step 9 native share sheet */ },
                )
            }
        }

        if (showBar) {
            WisepennyBottomBar(navController)
        }
    }
}
