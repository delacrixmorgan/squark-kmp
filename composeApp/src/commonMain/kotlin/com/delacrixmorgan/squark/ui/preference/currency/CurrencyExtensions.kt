package com.delacrixmorgan.squark.ui.preference.currency

import com.delacrixmorgan.squark.data.local.CurrencyLocalDataSource
import com.delacrixmorgan.squark.domain.model.Currency
import org.jetbrains.compose.resources.DrawableResource
import squark.composeapp.generated.resources.Res
import squark.composeapp.generated.resources.allDrawableResources
import squark.composeapp.generated.resources.ic_launcher_foreground

fun Currency.getFlagAsset(): DrawableResource =
    Res.allDrawableResources["ic_flag_${code.lowercase()}"] ?: Res.drawable.ic_launcher_foreground

fun Currency.getSymbol(): String =
    CurrencyLocalDataSource.currencySymbols[code] ?: code

fun Currency.getConversionLabel(): String {
    return "\$1 USD = ${getSymbol()} $rateToUSD"
}