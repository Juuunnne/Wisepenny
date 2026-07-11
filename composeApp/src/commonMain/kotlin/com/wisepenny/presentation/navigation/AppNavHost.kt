package com.wisepenny.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
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
import com.wisepenny.presentation.dashboard.DashboardScreen
import com.wisepenny.presentation.dashboard.DashboardViewModel
import com.wisepenny.presentation.goal.GoalDetailScreen
import com.wisepenny.presentation.goal.GoalListScreen
import com.wisepenny.presentation.goal.GoalViewModel
import com.wisepenny.presentation.learning.LearningListScreen
import com.wisepenny.presentation.learning.LearningViewModel
import com.wisepenny.presentation.learning.ModuleReaderScreen
import com.wisepenny.presentation.profile.ProfileScreen
import com.wisepenny.presentation.profile.ProfileViewModel
import com.wisepenny.presentation.theme.WisepennyColors
import org.koin.compose.viewmodel.koinViewModel

/**
 * The app's real navigation graph: a 4-tab bottom bar over the tab destinations,
 * with detail screens (goal, challenge) layered full-screen above. Replaces the
 * temporary in-memory router that hosted the screens before Step 6.
 */
@OptIn(ExperimentalComposeUiApi::class)
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
            composable<Accueil>(
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
            ) {
                val viewModel = koinViewModel<DashboardViewModel>()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                DashboardScreen(
                    state = state,
                    onGoalClick = { navController.navigate(GoalDetailRoute(it)) },
                    onChallengeClick = { navController.navigate(ChallengeDetailRoute(it)) },
                )
            }

            composable<Apprendre>(
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
            ) {
                val viewModel = koinViewModel<LearningViewModel>()
                val state by viewModel.listState.collectAsStateWithLifecycle()
                LearningListScreen(
                    state = state,
                    onModuleClick = { navController.navigate(ModuleReaderRoute(it)) },
                )
            }

            composable<Objectifs>(
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
            ) {
                val viewModel = koinViewModel<GoalViewModel>()
                val state by viewModel.listState.collectAsStateWithLifecycle()
                GoalListScreen(
                    state = state,
                    onGoalClick = { navController.navigate(GoalDetailRoute(it)) },
                    onAddGoal = { /* TODO: a create-goal form lands in a later step */ },
                )
            }

            composable<Profil>(
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
            ) {
                val viewModel = koinViewModel<ProfileViewModel>()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                ProfileScreen(
                    state = state,
                    onResetData = viewModel::onResetData,
                )
            }

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

            composable<ModuleReaderRoute> { entry ->
                val moduleId = entry.toRoute<ModuleReaderRoute>().moduleId
                val viewModel = koinViewModel<LearningViewModel>()
                LaunchedEffect(moduleId) { viewModel.selectModule(moduleId) }
                val state by viewModel.readerState.collectAsStateWithLifecycle()
                state?.let {
                    ModuleReaderScreen(
                        uiState = it,
                        onAdvance = { pageIndex -> viewModel.onAdvance(moduleId, pageIndex) },
                        onClose = { navController.popBackStack() },
                    )
                }
            }
        }

        // On iOS the leading-edge back swipe is always active and would otherwise
        // pop the current tab off the stack (feeling like "swipe to change tab").
        // While a top-level tab is showing, swallow it so tabs are tap-only.
        // Registered after NavHost so it takes priority over Navigation's own back
        // handling. On Android swallowTabBackGesture is false, so this stays off and
        // the system back gesture keeps working as before.
        BackHandler(enabled = swallowTabBackGesture && showBar) {
            // Intentionally do nothing: consume the gesture without navigating.
        }

        if (showBar) {
            WisepennyBottomBar(navController)
        }
    }
}
