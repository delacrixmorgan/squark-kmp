package com.delacrixmorgan.squark.ui.preference.settings.appinfo

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.delacrixmorgan.squark.nav.EffectEmitter
import com.delacrixmorgan.squark.nav.NavEffect
import org.koin.core.component.KoinComponent

class AppInfoViewModel : ViewModel(), KoinComponent {

    private val emitter = EffectEmitter<NavEffect>()
    val effects = emitter.effects

    fun onAction(navHostController: NavHostController, action: AppInfoAction) {
        when (action) {
            is AppInfoAction.OnDeveloperClicked -> {
                emitter.send(NavEffect.OpenUri("https://github.com/delacrixmorgan"))
            }
            is AppInfoAction.OnSourceCodeClicked -> {
                emitter.send(NavEffect.OpenUri("https://github.com/delacrixmorgan/squark-kmp"))
            }
            AppInfoAction.OnBackClicked -> navHostController.navigateUp()
        }
    }
}

sealed interface AppInfoAction {
    data object OnDeveloperClicked : AppInfoAction
    data object OnSourceCodeClicked : AppInfoAction
    data object OnBackClicked : AppInfoAction
}
