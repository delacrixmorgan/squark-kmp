package com.delacrixmorgan.squark.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Response of GET https://api.apilayer.com/currency_data/live
 *
 * `quotes` keys are concatenated currency pairs against the [source] base, e.g.
 * `USDMYR`, `USDAUD`.
 */
@Serializable
data class CurrencyLiveResponseDto(
    val success: Boolean = false,
    val timestamp: Long = 0L,
    val source: String = "",
    val quotes: Map<String, Double> = emptyMap(),
)
