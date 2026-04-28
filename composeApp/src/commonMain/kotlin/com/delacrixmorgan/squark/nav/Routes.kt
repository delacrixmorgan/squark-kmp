package com.delacrixmorgan.squark.nav

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    data object Table : Routes()

    @Serializable
    data class Preferences(val pickerTarget: CurrencyTarget = CurrencyTarget.Base) : Routes()

    @Serializable
    data class Currency(val pickerTarget: CurrencyTarget = CurrencyTarget.Base) : Routes()

    @Serializable
    data object Settings : Routes()

    @Serializable
    data object AppInfo : Routes()
}