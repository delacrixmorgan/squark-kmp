package com.delacrixmorgan.squark.nav.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.delacrixmorgan.squark.nav.CurrencyTarget
import com.delacrixmorgan.squark.nav.Routes
import com.delacrixmorgan.squark.ui.preference.currency.CurrencyScreen
import com.delacrixmorgan.squark.ui.preference.currency.CurrencyViewModel
import com.delacrixmorgan.squark.ui.preference.settings.SettingsScreen
import com.delacrixmorgan.squark.ui.preference.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PreferencesBottomNavHost(
    navHostController: NavHostController,
    bottomNavHostController: NavHostController,
    innerPadding: PaddingValues,
    pickerTarget: CurrencyTarget,
) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = bottomNavHostController,
        startDestination = Routes.Currency(pickerTarget)
    ) {
        composable<Routes.Currency> {
            val viewModel = koinViewModel<CurrencyViewModel>()
            CurrencyScreen(hostInnerPadding = innerPadding, state = viewModel.state.collectAsStateWithLifecycle().value, onAction = { viewModel.onAction(navHostController, it) })
        }
        composable<Routes.Settings> {
            val viewModel = koinViewModel<SettingsViewModel>()
            SettingsScreen(innerPadding, state = viewModel.state.collectAsStateWithLifecycle().value, effects = viewModel.effects, onAction = { viewModel.onAction(navHostController, it) })
        }
    }
}