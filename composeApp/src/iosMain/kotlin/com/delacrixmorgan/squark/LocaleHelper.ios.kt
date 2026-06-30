package com.delacrixmorgan.squark

import com.delacrixmorgan.squark.domain.model.FontScale
import com.delacrixmorgan.squark.domain.model.LocalePreference
import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

actual class LocaleHelper {

    actual fun getSystemLanguage(): LocalePreference {
        val preferred = NSLocale.preferredLanguages.firstOrNull() as? String ?: return LocalePreference.Default
        val code = preferred.substringBefore("-")
        return LocalePreference.entries.firstOrNull { it.code == code } ?: LocalePreference.Default
    }

    actual fun getFontScale(): FontScale = FontScale.Normal // TODO: map UIContentSizeCategory
}
