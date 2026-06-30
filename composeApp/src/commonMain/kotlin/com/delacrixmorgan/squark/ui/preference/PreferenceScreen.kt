package com.delacrixmorgan.squark.ui.preference

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.delacrixmorgan.squark.nav.CurrencyTarget
import com.delacrixmorgan.squark.nav.preference.PreferencesBottomNavItem
import com.delacrixmorgan.squark.nav.preference.PreferencesBottomNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    navHostController: NavHostController,
    pickerTarget: CurrencyTarget,
    bottomNavHostController: NavHostController = rememberNavController(),
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(bottomNavHostController) }
    ) { innerPadding ->
        PreferencesBottomNavHost(navHostController, bottomNavHostController, innerPadding, pickerTarget)
    }
}

@Composable
private fun BottomNavigationBar(navHostController: NavHostController) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

    NavigationBar {
        PreferencesBottomNavItem.entries.forEach { navItem ->
            val selected = navBackStackEntry?.destination?.hierarchy
                ?.any { it.hasRoute(navItem.route::class) } == true
            NavigationBarItem(
                icon = { Icon(navItem.icon, contentDescription = navItem.title) },
                selected = selected,
                onClick = {
                    if (selected) return@NavigationBarItem
                    navHostController.navigate(navItem.route) {
                        popUpTo(navHostController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
