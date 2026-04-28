package com.delacrixmorgan.squark.data.mapper

import com.delacrixmorgan.squark.data.local.CurrencyLocalDataSource
import com.delacrixmorgan.squark.data.local.entity.CurrencyRatesEntity
import com.delacrixmorgan.squark.data.remote.dto.CurrencyLiveResponseDto
import com.delacrixmorgan.squark.domain.model.Currency
import kotlin.time.Clock

/**
 * Converts between the currency DTO, the cached entity, and the domain model.
 *
 * The API quotes are keyed as `<source><code>` (e.g. `USDMYR`); the entity and model
 * use the plain 3-letter code. Display names come from the bundled
 * [CurrencyLocalDataSource.currencyNames]; codes without a known name are dropped.
 */
class CurrencyMapper {

    /** Plain code -> rate-to-source map from the API response. */
    fun dtoToEntity(dto: CurrencyLiveResponseDto): CurrencyRatesEntity = CurrencyRatesEntity(
        // The API omits the source<->source pair (e.g. no USDUSD), so re-inject the
        // base currency at rate 1.0 — otherwise USD itself disappears after a refresh.
        quotes = dto.quotes.entries.associate { (pair, rate) ->
            pair.removePrefix(dto.source) to rate
        } + (dto.source to 1.0),
        updatedAt = Clock.System.now(),
    )

    fun dtoToModel(dto: CurrencyLiveResponseDto): List<Currency> =
        ratesToModel(dtoToEntity(dto).quotes)

    fun entityToModel(entity: CurrencyRatesEntity): List<Currency> =
        ratesToModel(entity.quotes)

    fun modelToEntity(model: List<Currency>): CurrencyRatesEntity = CurrencyRatesEntity(
        quotes = model.associate { it.code to it.rateToUSD },
        updatedAt = Clock.System.now(),
    )

    private fun ratesToModel(rates: Map<String, Double>): List<Currency> =
        rates.mapNotNull { (code, rate) ->
            val name = CurrencyLocalDataSource.currencyNames[code] ?: return@mapNotNull null
            Currency(code = code, name = name, rateToUSD = rate)
        }.sortedBy { it.code }
}
