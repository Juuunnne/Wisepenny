package com.wisepenny.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
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

@Composable
fun WisepennyBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor = WisepennyColors.SurfaceElevated) {
        TABS.forEach { tab ->
            val selected = currentDestination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = WisepennyColors.AccentMint,
                    selectedTextColor = WisepennyColors.AccentMint,
                    unselectedIconColor = WisepennyColors.TextTertiary,
                    unselectedTextColor = WisepennyColors.TextTertiary,
                    indicatorColor = WisepennyColors.BackgroundPrimary,
                ),
            )
        }
    }
}
