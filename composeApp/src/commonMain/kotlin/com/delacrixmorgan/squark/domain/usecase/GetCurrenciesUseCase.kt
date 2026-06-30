package com.delacrixmorgan.squark.domain.usecase

import com.delacrixmorgan.squark.domain.model.Currency
import com.delacrixmorgan.squark.domain.model.result.Result
import com.delacrixmorgan.squark.domain.model.result.exception.DataError
import com.delacrixmorgan.squark.domain.model.result.map
import com.delacrixmorgan.squark.domain.repository.CurrencyRepository
import com.delacrixmorgan.squark.domain.repository.DataAccessStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCurrenciesUseCase(
    private val repository: CurrencyRepository,
) {
    operator fun invoke(
        query: String = "",
        strategy: DataAccessStrategy = DataAccessStrategy.FromLocalOnlyIfNotExpired,
    ): Flow<Result<List<Currency>, DataError>> =
        repository.getCurrencies(strategy).map { result ->
            result.map { currencies -> currencies.filterByQuery(query) }
        }
}

/** Filters by case-insensitive match against the currency code or name. */
fun List<Currency>.filterByQuery(query: String): List<Currency> {
    if (query.isBlank()) return this
    val lowerQuery = query.lowercase()
    return filter { currency ->
        currency.code.lowercase().contains(lowerQuery) ||
            currency.name.lowercase().contains(lowerQuery)
    }
}
