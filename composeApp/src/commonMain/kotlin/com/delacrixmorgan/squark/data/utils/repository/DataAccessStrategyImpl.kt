package com.delacrixmorgan.squark.data.utils.repository

import com.delacrixmorgan.squark.data.utils.CACHE_EXPIRATION_24_HOURS_IN_MINUTES
import com.delacrixmorgan.squark.data.utils.Log
import com.delacrixmorgan.squark.data.utils.isLessThanMinutesAgo
import com.delacrixmorgan.squark.domain.model.result.Result
import com.delacrixmorgan.squark.domain.model.result.exception.DataError
import com.delacrixmorgan.squark.domain.model.result.exception.DataError.LocalError
import com.delacrixmorgan.squark.domain.model.result.exception.DataError.RemoteError
import com.delacrixmorgan.squark.domain.model.result.failure
import com.delacrixmorgan.squark.domain.model.result.fold
import com.delacrixmorgan.squark.domain.model.result.get
import com.delacrixmorgan.squark.domain.model.result.getOrNull
import com.delacrixmorgan.squark.domain.model.result.map
import com.delacrixmorgan.squark.domain.model.result.success
import com.delacrixmorgan.squark.domain.repository.DataAccessStrategy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Instant

/**
 * Reusable Clean-Architecture data access strategy. Multiplatform/Koin port of the
 * canonical Hilt/JVM implementation:
 *  - `java.util.Date` -> [kotlinx.datetime.Instant]
 *  - `ConcurrentHashMap` debounce -> [Mutex]-guarded set of in-flight resource ids
 *  - no DI annotations; provided via Koin `single { }`
 *
 * The cache window is expressed in minutes (default = 24h, [CACHE_EXPIRATION_24_HOURS_IN_MINUTES]).
 */
class DataAccessStrategyImpl {

    // Used to debounce/throttle API calls when parallel requests are made for the same resource.
    private val ongoingRemoteFetches: MutableSet<String> = mutableSetOf()
    private val ongoingRemoteFetchesLock = Mutex()

    /**
     * Implements the data access strategy to retrieve a single resource.
     * Note: [resourceId] should be a unique id for the resource being accessed.
     */
    fun <Model, Entity, Dto> performGetOperation(
        resourceId: String,
        getFromRemote: suspend () -> Result<Dto, RemoteError>,
        getFromLocal: () -> Flow<Result<Entity, LocalError>>,
        saveToLocal: suspend (Entity) -> Result<Unit, LocalError>,
        dtoToModelMapper: suspend (Dto) -> Model,
        entityToModelMapper: suspend (Entity) -> Model,
        modelToEntityMapper: suspend (Model) -> Entity,
        getUpdatedAt: (Entity) -> Instant,
        cacheExpirationMinutes: Int = CACHE_EXPIRATION_24_HOURS_IN_MINUTES,
        strategy: DataAccessStrategy = DataAccessStrategy.FromLocalOnlyIfNotExpired,
    ): Flow<Result<Model, DataError>> = flow {
        var ignoreLocalData = strategy == DataAccessStrategy.FromRemoteOnly
        val ignoreRemoteData = strategy == DataAccessStrategy.FromLocalOnly
        val emitErrorInLocalData = strategy == DataAccessStrategy.FromLocalOnly
        var forceRemoteFetchNeeded = strategy != DataAccessStrategy.FromLocalOnlyIfNotExpired
        getFromLocal().collect { localRes ->
            // Emit cached value
            if (ignoreLocalData) {
                ignoreLocalData = false // Only first emitted value has to be ignored
            } else {
                localRes.success {
                    Log.v("performGetOperation(): $resourceId | emit cached value | $it")
                    emit(localRes.map { entity -> entityToModelMapper(entity) })
                }
                localRes.failure {
                    Log.v("performGetOperation(): $resourceId | emit error in cached value | $it")
                    if (emitErrorInLocalData) emit(Result.error(it))
                }
            }
            // Fetch from backend if needed
            val remoteFetchNeeded = !ignoreRemoteData && (
                forceRemoteFetchNeeded ||
                    localRes.getOrNull() == null ||
                    !getUpdatedAt(localRes.get()).isLessThanMinutesAgo(cacheExpirationMinutes)
                )
            if (remoteFetchNeeded && lockIfNoOngoingRemoteFetch(resourceId)) {
                try {
                    Log.v("performGetOperation(): $resourceId | fetch from network")
                    forceRemoteFetchNeeded = false
                    getFromRemote().map { dto -> dtoToModelMapper(dto) }.fold(
                        success = {
                            Log.v("performGetOperation(): $resourceId | save data $it")
                            saveToLocal(modelToEntityMapper(it))
                        },
                        failure = {
                            Log.v("performGetOperation(): $resourceId | fetch from network failed $it")
                            emit(Result.error(it))
                        },
                    )
                } catch (e: CancellationException) {
                    throw e // Coroutine cancellation is cooperative
                } catch (e: Exception) {
                    emit(Result.error(DataError.GenericDataError(e)))
                } finally {
                    unlockRemoteFetch(resourceId)
                }
            }
        }
    }.distinctUntilChanged()

    /**
     * Implements the data access strategy to retrieve a list of data.
     * Note: [resourceId] should be a unique id for the resource being accessed.
     */
    fun <Model, Entity, Dto> performGetListOperation(
        resourceId: String,
        getFromRemote: suspend () -> Result<List<Dto>, RemoteError>,
        getFromLocal: () -> Flow<Result<List<Entity>, LocalError>>,
        saveToLocal: suspend (List<Entity>) -> Result<Unit, LocalError>,
        dtoToModelListMapper: suspend (List<Dto>) -> List<Model>,
        entityToModelListMapper: suspend (List<Entity>) -> List<Model>,
        modelToEntityListMapper: suspend (List<Model>) -> List<Entity>,
        getUpdatedAt: (List<Entity>) -> Instant,
        cacheExpirationMinutes: Int = CACHE_EXPIRATION_24_HOURS_IN_MINUTES,
        strategy: DataAccessStrategy = DataAccessStrategy.FromLocalOnlyIfNotExpired,
        omitResultIfEmptyList: Boolean = false,
    ): Flow<Result<List<Model>, DataError>> = flow {
        var ignoreLocalData = strategy == DataAccessStrategy.FromRemoteOnly
        val ignoreRemoteData = strategy == DataAccessStrategy.FromLocalOnly
        var forceRemoteFetchNeeded = strategy != DataAccessStrategy.FromLocalOnlyIfNotExpired
        getFromLocal().collect { localRes ->
            // Emit cached value
            if (ignoreLocalData) {
                ignoreLocalData = false // Only first emitted value has to be ignored
            } else {
                localRes.success {
                    if (omitResultIfEmptyList && it.isEmpty()) {
                        Log.v("performGetListOperation(): $resourceId | emit list omitted")
                        return@success
                    }
                    Log.v("performGetListOperation(): $resourceId | emit cached value | $it")
                    emit(localRes.map { entity -> entityToModelListMapper(entity) })
                }
            }
            // Fetch from backend if needed
            val remoteFetchNeeded = !ignoreRemoteData && (
                forceRemoteFetchNeeded ||
                    localRes.getOrNull().isNullOrEmpty() ||
                    !getUpdatedAt(localRes.get()).isLessThanMinutesAgo(cacheExpirationMinutes)
                )
            if (remoteFetchNeeded && lockIfNoOngoingRemoteFetch(resourceId)) {
                try {
                    Log.v("performGetListOperation(): $resourceId | fetch from network")
                    forceRemoteFetchNeeded = false
                    getFromRemote().map { dtoList -> dtoToModelListMapper(dtoList) }.fold(
                        success = {
                            Log.v("performGetListOperation(): $resourceId | save data $it")
                            saveToLocal(modelToEntityListMapper(it))
                        },
                        failure = {
                            Log.v("performGetListOperation(): $resourceId | fetch from network failed $it")
                            emit(Result.error(it))
                        },
                    )
                } catch (e: CancellationException) {
                    throw e // Coroutine cancellation is cooperative
                } catch (e: Exception) {
                    emit(Result.error(DataError.GenericDataError(e)))
                } finally {
                    unlockRemoteFetch(resourceId)
                }
            }
        }
    }.distinctUntilChanged()

    private suspend fun lockIfNoOngoingRemoteFetch(resourceId: String): Boolean =
        ongoingRemoteFetchesLock.withLock { ongoingRemoteFetches.add(resourceId) }

    private suspend fun unlockRemoteFetch(resourceId: String) {
        ongoingRemoteFetchesLock.withLock { ongoingRemoteFetches.remove(resourceId) }
    }
}
