package com.delacrixmorgan.squark.data.utils.repository

import com.delacrixmorgan.squark.domain.model.result.Result
import com.delacrixmorgan.squark.domain.model.result.exception.DataError
import com.delacrixmorgan.squark.domain.model.result.exception.DataError.LocalError
import com.delacrixmorgan.squark.domain.model.result.exception.DataError.RemoteError
import com.delacrixmorgan.squark.domain.model.result.getOrNull
import com.delacrixmorgan.squark.domain.repository.DataAccessStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

private data class TestEntity(val value: String, val updatedAt: Instant)

class DataAccessStrategyImplTest {

    private val strategy = DataAccessStrategyImpl()
    private val cacheWindowMinutes = 60

    private fun freshEntity(value: String) =
        TestEntity(value, Clock.System.now())

    private fun staleEntity(value: String) =
        TestEntity(value, Clock.System.now() - 120.minutes)

    private fun run(
        local: TestEntity,
        remoteValue: String = "remote",
        remoteFails: Boolean = false,
        accessStrategy: DataAccessStrategy,
        onRemoteCall: () -> Unit = {},
    ) = strategy.performGetOperation<String, TestEntity, String>(
        resourceId = "test",
        getFromRemote = {
            onRemoteCall()
            if (remoteFails) Result.error(RemoteError("boom")) else Result.success(remoteValue)
        },
        getFromLocal = { localFlow },
        saveToLocal = { entity ->
            localFlow.value = Result.success(entity)
            Result.success(Unit)
        },
        dtoToModelMapper = { it },
        entityToModelMapper = { it.value },
        modelToEntityMapper = { TestEntity(it, Clock.System.now()) },
        getUpdatedAt = { it.updatedAt },
        cacheExpirationMinutes = cacheWindowMinutes,
        strategy = accessStrategy,
    ).also { localFlow.value = Result.success(local) }

    private lateinit var localFlow: MutableStateFlow<Result<TestEntity, LocalError>>

    private fun newLocalFlow() {
        localFlow = MutableStateFlow(Result.success(TestEntity("placeholder", Instant.DISTANT_PAST)))
    }

    @Test
    fun `FromLocalOnlyIfNotExpired with fresh cache emits local and skips remote`() = runTest {
        newLocalFlow()
        var remoteCalls = 0
        val flow = run(
            local = freshEntity("cached"),
            accessStrategy = DataAccessStrategy.FromLocalOnlyIfNotExpired,
            onRemoteCall = { remoteCalls++ },
        )

        val emissions = flow.take(1).toList().map { it.getOrNull() }

        assertEquals(listOf("cached"), emissions)
        assertEquals(0, remoteCalls)
    }

    @Test
    fun `FromLocalOnlyIfNotExpired with expired cache emits local then remote`() = runTest {
        newLocalFlow()
        var remoteCalls = 0
        val flow = run(
            local = staleEntity("cached"),
            accessStrategy = DataAccessStrategy.FromLocalOnlyIfNotExpired,
            onRemoteCall = { remoteCalls++ },
        )

        val emissions = flow.take(2).toList().map { it.getOrNull() }

        assertEquals(listOf("cached", "remote"), emissions)
        assertEquals(1, remoteCalls)
    }

    @Test
    fun `FromRemoteOnly ignores first local emission and emits remote`() = runTest {
        newLocalFlow()
        var remoteCalls = 0
        val flow = run(
            local = freshEntity("cached"),
            accessStrategy = DataAccessStrategy.FromRemoteOnly,
            onRemoteCall = { remoteCalls++ },
        )

        val emissions = flow.take(1).toList().map { it.getOrNull() }

        assertEquals(listOf("remote"), emissions)
        assertEquals(1, remoteCalls)
    }

    @Test
    fun `FromLocalOnly emits local and never fetches`() = runTest {
        newLocalFlow()
        var remoteCalls = 0
        val flow = run(
            local = staleEntity("cached"),
            accessStrategy = DataAccessStrategy.FromLocalOnly,
            onRemoteCall = { remoteCalls++ },
        )

        val emissions = flow.take(1).toList().map { it.getOrNull() }

        assertEquals(listOf("cached"), emissions)
        assertEquals(0, remoteCalls)
    }

    @Test
    fun `remote failure still emits the cached value`() = runTest {
        newLocalFlow()
        val flow: Flow<Result<String, DataError>> = run(
            local = staleEntity("cached"),
            remoteFails = true,
            accessStrategy = DataAccessStrategy.FromLocalOnlyIfNotExpired,
        )

        val emissions = flow.take(2).toList()

        assertEquals("cached", emissions[0].getOrNull())
        assertEquals(true, emissions[1] is Result.Error)
    }
}
