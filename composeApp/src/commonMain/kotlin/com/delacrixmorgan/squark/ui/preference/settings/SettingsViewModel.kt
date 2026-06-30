package com.delacrixmorgan.squark.ui.preference.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.delacrixmorgan.squark.domain.model.ThemePreference
import com.delacrixmorgan.squark.domain.repository.PreferencesRepository
import com.delacrixmorgan.squark.getVersionCode
import com.delacrixmorgan.squark.getVersionName
import com.delacrixmorgan.squark.nav.EffectEmitter
import com.delacrixmorgan.squark.nav.NavEffect
import com.delacrixmorgan.squark.nav.Routes
import com.delacrixmorgan.squark.rateUsStoreLink
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class SettingsViewModel(
    private val preferences: PreferencesRepository,
) : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val emitter = EffectEmitter<NavEffect>()
    val effects = emitter.effects

    init {
        _state.update { it.copy(version = "${getVersionName()} (${getVersionCode()})") }
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            preferences.getTheme().collect { theme -> _state.update { it.copy(theme = theme) } }
        }
        viewModelScope.launch {
            preferences.getReduceBounciness().collect { reduceBounciness ->
                _state.update { it.copy(reduceBounciness = reduceBounciness) }
            }
        }
    }

    fun onAction(navHostController: NavHostController, action: SettingsAction) {
        when (action) {
            is SettingsAction.ToggleThemeVisibility -> {
                _state.value = _state.value.copy(showTheme = action.show)
            }
            is SettingsAction.OnThemeSelected -> {
                viewModelScope.launch { preferences.saveTheme(action.theme) }
            }
            is SettingsAction.OnReduceBouncinessToggled -> {
                viewModelScope.launch { preferences.saveReduceBounciness(action.value) }
            }
            SettingsAction.OpenAppInfo -> {
                navHostController.navigate(Routes.AppInfo)
            }
            SettingsAction.OpenPrivacyPolicy -> {
                emitter.send(NavEffect.OpenUri("https://github.com/delacrixmorgan/squark-kmp/blob/main/PRIVACY_POLICY.md"))
            }
            SettingsAction.OpenSendFeedback -> {
                emitter.send(NavEffect.OpenUri("mailto:delacrixmorgan@gmail.com?subject=Squark - App Feedback"))
            }
            SettingsAction.OpenRateUs -> {
                emitter.send(NavEffect.OpenUri(rateUsStoreLink))
            }
        }
    }
}

data class SettingsUiState(
    val version: String = "",
    val theme: ThemePreference = ThemePreference.Default,
    val showTheme: Boolean = false,
    val reduceBounciness: Boolean = false,
)

sealed interface SettingsAction {
    data class ToggleThemeVisibility(val show: Boolean) : SettingsAction
    data class OnThemeSelected(val theme: ThemePreference) : SettingsAction
    data class OnReduceBouncinessToggled(val value: Boolean) : SettingsAction

    data object OpenAppInfo : SettingsAction
    data object OpenPrivacyPolicy : SettingsAction
    data object OpenSendFeedback : SettingsAction
    data object OpenRateUs : SettingsAction
}