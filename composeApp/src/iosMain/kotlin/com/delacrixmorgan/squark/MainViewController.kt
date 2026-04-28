package com.delacrixmorgan.squark

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.delacrixmorgan.squark.domain.model.ThemePreference
import com.delacrixmorgan.squark.ui.preference.PreferenceViewModel
import com.delacrixmorgan.squark.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

fun MainViewController() = ComposeUIViewController {
    val theme = remember { mutableStateOf(ThemePreference.Default) }
    val preferenceViewModel = koinViewModel<PreferenceViewModel>()
    val preferenceState = preferenceViewModel.state.collectAsState().value

    AppTheme(theme = preferenceState.theme) {
        App(Modifier)
    }
}