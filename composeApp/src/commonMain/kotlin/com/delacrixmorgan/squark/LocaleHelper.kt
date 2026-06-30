package com.delacrixmorgan.squark

import com.delacrixmorgan.squark.domain.model.FontScale
import com.delacrixmorgan.squark.domain.model.LocalePreference

/**
 * Reads device configuration (locale, font scale) that we report as analytics user properties.
 * Implemented per-platform and provided via [platformModule].
 */
expect class LocaleHelper {
    fun getSystemLanguage(): LocalePreference
    fun getFontScale(): FontScale
}
