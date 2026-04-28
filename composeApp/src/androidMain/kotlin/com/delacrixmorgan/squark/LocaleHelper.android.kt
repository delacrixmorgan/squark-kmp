package com.delacrixmorgan.squark

import android.content.Context
import androidx.core.os.ConfigurationCompat
import com.delacrixmorgan.squark.domain.model.FontScale
import com.delacrixmorgan.squark.domain.model.LocalePreference
import com.delacrixmorgan.squark.domain.model.toFontScale

actual class LocaleHelper(private val context: Context) {

    actual fun getSystemLanguage(): LocalePreference {
        val locale = ConfigurationCompat.getLocales(context.resources.configuration)[0]
        return LocalePreference.entries.firstOrNull { it.code == locale?.language } ?: LocalePreference.Default
    }

    actual fun getFontScale(): FontScale =
        context.resources.configuration.fontScale.toFontScale()
}
