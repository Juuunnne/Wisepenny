package com.wisepenny.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.wisepenny.presentation.theme.Motion
import com.wisepenny.presentation.theme.Spacing
import com.wisepenny.presentation.theme.WisepennyColors

private data class TabItem(val route: Any, val label: String, val icon: ImageVector)

private val TABS = listOf(
    TabItem(Accueil, "Accueil", Icons.Filled.GridView),
    TabItem(Apprendre, "Apprendre", Icons.Filled.School),
    TabItem(Objectifs, "Objectifs", Icons.Filled.TrackChanges),
    TabItem(Profil, "Profil", Icons.Filled.Person),
)

/** The route classes that should display the bottom bar (the 4 tabs). */
internal val TAB_ROUTES: List<Any> = TABS.map { it.route }

private val BarHeight = 56.dp

/**
 * A hand-rolled tab bar rather than Material 3's [androidx.compose.material3.NavigationBar].
 * The M3 bar animates a pill-shaped indicator behind the selected icon and draws an ink
 * ripple from the touch point — both read as unmistakably Android on iOS, where a tab bar
 * only ever re-tints its icon. Here the tint fade is the sole motion.
 */
@Composable
fun WisepennyBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Column(modifier = Modifier.background(WisepennyColors.SurfaceElevated)) {
        HorizontalDivider(thickness = 1.dp, color = WisepennyColors.BorderSubtle)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(BarHeight),
        ) {
            TABS.forEach { tab ->
                val selected = currentDestination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
                TabBarItem(
                    tab = tab,
                    isSelected = selected,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun TabBarItem(
    tab: TabItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint by animateColorAsState(
        targetValue = if (isSelected) WisepennyColors.AccentMint else WisepennyColors.TextTertiary,
        animationSpec = tween(durationMillis = Motion.fastMillis, easing = Motion.defaultEasing),
        label = "tabTint",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(BarHeight)
            // Named `isSelected` so this doesn't shadow SemanticsPropertyReceiver.selected.
            .semantics {
                role = Role.Tab
                selected = isSelected
            }
            // indication = null: no ink ripple, which is what makes the bar feel Android.
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = tab.label,
            color = tint,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            maxLines = 1,
        )
    }
}
