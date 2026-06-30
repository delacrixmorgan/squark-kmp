package com.delacrixmorgan.squark.domain.repository

import com.delacrixmorgan.squark.domain.model.Currency
import com.delacrixmorgan.squark.domain.model.result.Result
import com.delacrixmorgan.squark.domain.model.result.exception.DataError
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface CurrencyRepository {
    fun getCurrencies(
        strategy: DataAccessStrategy = DataAccessStrategy.FromLocalOnlyIfNotExpired,
    ): Flow<Result<List<Currency>, DataError>>

    /** Emits when the cached rates were last persisted, or `null` if never refreshed. */
    fun getRatesUpdatedAt(): Flow<Instant?>
}
