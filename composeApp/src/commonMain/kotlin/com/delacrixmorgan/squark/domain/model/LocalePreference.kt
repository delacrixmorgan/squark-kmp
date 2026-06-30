package com.delacrixmorgan.squark.domain.model

/**
 * Languages we distinguish when reporting the device locale as an analytics user property, keyed by
 * ISO 639-1 [code]. The app currently ships only the default `values/` bundle (English); add entries
 * here for any additional language you want to segment on. Anything unmatched maps to [Default].
 */
enum class LocalePreference(val code: String) {
    En("en");

    companion object {
        val Default = En
    }
}
