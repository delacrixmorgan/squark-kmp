package com.delacrixmorgan.squark.ui.table

import kotlin.math.abs
import kotlin.math.round

/**
 * Formats a number with abbreviations for large values:
 * Q (quadrillion), T (trillion), B (billion), M (million)
 */
fun Double.formatNumber(): String {
    val absValue = abs(this)
    return when {
        absValue >= 1_000_000_000_000_000.0 ->
            (this / 1_000_000_000_000_000.0).formatTwoDecimals() + "Q"

        absValue >= 1_000_000_000_000.0 ->
            (this / 1_000_000_000_000.0).formatTwoDecimals() + "T"

        absValue >= 1_000_000_000.0 ->
            (this / 1_000_000_000.0).formatTwoDecimals() + "B"

        absValue >= 1_000_000.0 ->
            (this / 1_000_000.0).formatTwoDecimals() + "M"

        absValue >= 1_000.0 ->
            (this / 1_000.0).formatTwoDecimals() + "K"

        else -> this.formatTwoDecimals()
    }
}

fun Double.formatTwoDecimals(): String {
    val rounded = round(this * 100).toLong()
    val integerPart = rounded / 100
    val fractionalPart = abs(rounded % 100).toString().padStart(2, '0')
    return "$integerPart.$fractionalPart"
}