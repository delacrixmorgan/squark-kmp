package com.delacrixmorgan.squark.data.local.entity

import kotlin.time.Instant

/**
 * Locally cached snapshot of currency rates.
 *
 * @param quotes raw API pairs keyed against USD, e.g. `USDMYR` -> 4.06.
 * @param updatedAt when this snapshot was persisted; drives the 24h cache window.
 */
data class CurrencyRatesEntity(
    val quotes: Map<String, Double>,
    val updatedAt: Instant,
)
