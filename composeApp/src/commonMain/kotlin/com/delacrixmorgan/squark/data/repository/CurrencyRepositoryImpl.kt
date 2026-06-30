package com.delacrixmorgan.squark.data.repository

import com.delacrixmorgan.squark.data.local.CurrencyCacheLocalDataSource
import com.delacrixmorgan.squark.data.mapper.CurrencyMapper
import com.delacrixmorgan.squark.data.remote.CurrencyRemoteDataSource
import com.delacrixmorgan.squark.data.utils.CACHE_EXPIRATION_24_HOURS_IN_MINUTES
import com.delacrixmorgan.squark.data.utils.repository.DataAccessStrategyImpl
import com.delacrixmorgan.squark.domain.model.Currency
import com.delacrixmorgan.squark.domain.model.result.Result
import com.delacrixmorgan.squark.domain.model.result.exception.DataError
import com.delacrixmorgan.squark.domain.model.result.getOrNull
import com.delacrixmorgan.squark.domain.repository.CurrencyRepository
import com.delacrixmorgan.squark.domain.repository.DataAccessStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

class CurrencyRepositoryImpl(
    private val remoteDataSource: CurrencyRemoteDataSource,
    private val cacheLocalDataSource: CurrencyCacheLocalDataSource,
    private val dataAccessStrategy: DataAccessStrategyImpl,
    private val mapper: CurrencyMapper,
) : CurrencyRepository {

    override fun getCurrencies(
        strategy: DataAccessStrategy,
    ): Flow<Result<List<Currency>, DataError>> = dataAccessStrategy.performGetOperation(
        resourceId = "getCurrencies",
        getFromRemote = { remoteDataSource.getLiveRates() },
        getFromLocal = { cacheLocalDataSource.getRates() },
        saveToLocal = { cacheLocalDataSource.saveRates(it) },
        dtoToModelMapper = { mapper.dtoToModel(it) },
        entityToModelMapper = { mapper.entityToModel(it) },
        modelToEntityMapper = { mapper.modelToEntity(it) },
        getUpdatedAt = { it.updatedAt },
        cacheExpirationMinutes = CACHE_EXPIRATION_24_HOURS_IN_MINUTES,
        strategy = strategy,
    )

    override fun getRatesUpdatedAt(): Flow<Instant?> =
        cacheLocalDataSource.getRates().map { result ->
            result.getOrNull()?.updatedAt?.takeIf { it != Instant.DISTANT_PAST }
        }

    // Debug
//    override fun getCurrencies(
//        strategy: DataAccessStrategy,
//    ): Flow<Result<List<Currency>, DataError>> = flow {
//        emit(cacheLocalDataSource.getRates().first().map { mapper.entityToModel(it) })
//    }
//
//    override fun getRatesUpdatedAt(): Flow<Instant?> =
//        flow { emit(Clock.System.now())
}
