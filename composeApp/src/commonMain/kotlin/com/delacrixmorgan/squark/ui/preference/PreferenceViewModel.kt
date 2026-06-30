package com.delacrixmorgan.squark.ui.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacrixmorgan.squark.domain.model.ThemePreference
import com.delacrixmorgan.squark.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class PreferenceViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(PreferenceUiState())
    val state: StateFlow<PreferenceUiState> = _state.asStateFlow()

    init {
        observeTheme()
    }

    private fun observeTheme() {
        viewModelScope.launch {
            preferencesRepository.getTheme().collect { theme ->
                _state.update { it.copy(theme = theme) }
            }
        }
    }

    fun onAction(action: PreferenceAction) {
        when (action) {
            is PreferenceAction.OnThemeSelected -> {
                _state.update { it.copy(theme = action.theme) }
                viewModelScope.launch { preferencesRepository.saveTheme(action.theme) }
            }
        }
    }
}

data class PreferenceUiState(
    val theme: ThemePreference = ThemePreference.Default,
)

sealed interface PreferenceAction {
    data class OnThemeSelected(val theme: ThemePreference) : PreferenceAction
}
