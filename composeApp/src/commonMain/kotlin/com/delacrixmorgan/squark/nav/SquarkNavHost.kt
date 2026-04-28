package com.delacrixmorgan.squark.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.delacrixmorgan.squark.ui.preference.PreferencesScreen
import com.delacrixmorgan.squark.ui.preference.settings.appinfo.AppInfoScreen
import com.delacrixmorgan.squark.ui.preference.settings.appinfo.AppInfoViewModel
import com.delacrixmorgan.squark.ui.table.TableScreen
import com.delacrixmorgan.squark.ui.table.TableViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SquarkNavHost(navHostController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navHostController,
        startDestination = Routes.Table
    ) {
        composable<Routes.Table> {
            val tableViewModel = koinViewModel<TableViewModel>()
            val tableState = tableViewModel.state.collectAsStateWithLifecycle().value
            TableScreen(
                state = tableState,
                onAction = { tableViewModel.onAction(navHostController, it) },
            )
        }
        composable<Routes.Preferences> {
            PreferencesScreen(
                navHostController = navHostController,
                pickerTarget = it.toRoute<Routes.Preferences>().pickerTarget,
            )
        }

        composable<Routes.AppInfo> {
            val viewModel = koinViewModel<AppInfoViewModel>()
            AppInfoScreen(effects = viewModel.effects, onAction = { viewModel.onAction(navHostController, it) })
        }
    }
}
