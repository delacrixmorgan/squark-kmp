package com.delacrixmorgan.squark.data.local

import com.delacrixmorgan.squark.data.local.entity.CurrencyRatesEntity
import com.delacrixmorgan.squark.domain.model.result.Result
import com.delacrixmorgan.squark.domain.model.result.exception.DataError.LocalError
import kotlinx.coroutines.flow.Flow

interface CurrencyCacheLocalDataSource {
    fun getRates(): Flow<Result<CurrencyRatesEntity, LocalError>>
    suspend fun saveRates(entity: CurrencyRatesEntity): Result<Unit, LocalError>
}
