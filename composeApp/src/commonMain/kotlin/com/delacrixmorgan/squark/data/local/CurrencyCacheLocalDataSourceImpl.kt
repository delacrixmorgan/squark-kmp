package com.delacrixmorgan.squark.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.delacrixmorgan.squark.data.local.entity.CurrencyRatesEntity
import com.delacrixmorgan.squark.data.mapper.CurrencyMapper
import com.delacrixmorgan.squark.data.remote.dto.CurrencyLiveResponseDto
import com.delacrixmorgan.squark.domain.model.result.Result
import com.delacrixmorgan.squark.domain.model.result.exception.DataError.LocalError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import squark.composeapp.generated.resources.Res
import kotlin.time.Instant

class CurrencyCacheLocalDataSourceImpl : CurrencyCacheLocalDataSource, KoinComponent {
    companion object {
        private val KEY_RATES_JSON = stringPreferencesKey("aRtZ5q8wL2cV0nKx3bYp")
        private val KEY_RATES_UPDATED_AT = longPreferencesKey("Hm7Tg1Wd9sQ4uEr6zXoN")
    }

    private val dataStore: DataStore<Preferences> by inject(qualifier = named(LocalDataStore.Preferences.name))
    private val currencyMapper: CurrencyMapper by inject()
    private val json = Json

    override fun getRates(): Flow<Result<CurrencyRatesEntity, LocalError>> =
        dataStore.data
            .map<Preferences, Result<CurrencyRatesEntity, LocalError>> { prefs ->
                val ratesJson = prefs[KEY_RATES_JSON]
                val entity = if (ratesJson != null) {
                    CurrencyRatesEntity(
                        quotes = json.decodeFromString<Map<String, Double>>(ratesJson),
                        updatedAt = Instant.fromEpochMilliseconds(prefs[KEY_RATES_UPDATED_AT] ?: 0L),
                    )
                } else {
                    val bytes = Res.readBytes("files/data_currency.json")
                    val dto = json.decodeFromString<CurrencyLiveResponseDto>(bytes.decodeToString())
                    CurrencyRatesEntity(
                        quotes = currencyMapper.dtoToEntity(dto).quotes,
                        updatedAt = Instant.DISTANT_PAST,
                    )
                }
                Result.success(entity)
            }
            .catch { emit(Result.error(LocalError(message = it.message, cause = it))) }

    override suspend fun saveRates(entity: CurrencyRatesEntity): Result<Unit, LocalError> = try {
        dataStore.edit { prefs ->
            prefs[KEY_RATES_JSON] = json.encodeToString(entity.quotes)
            prefs[KEY_RATES_UPDATED_AT] = entity.updatedAt.toEpochMilliseconds()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.error(LocalError(message = e.message, cause = e))
    }
}
