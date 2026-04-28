package com.delacrixmorgan.squark.analytics

import com.delacrixmorgan.squark.LocaleHelper
import com.delacrixmorgan.squark.domain.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UserPropertyReporter(
    private val analytics: Analytics,
    private val preferences: PreferencesRepository,
    private val localeHelper: LocaleHelper,
) {
    fun start(scope: CoroutineScope) {
        scope.launch {
            analytics.setUserProperty(FONT_SCALE, localeHelper.getFontScale().name)
        }
        preferences.getTheme()
            .onEach { analytics.setUserProperty(THEME, it.name) }
            .launchIn(scope)
        preferences.getReduceBounciness()
            .onEach { analytics.setUserProperty(REDUCE_BOUNCINESS, it.toString()) }
            .launchIn(scope)
    }

    private companion object {
        const val THEME = "theme"
        const val FONT_SCALE = "font_scale"
        const val REDUCE_BOUNCINESS = "reduce_bounciness"
    }
}
