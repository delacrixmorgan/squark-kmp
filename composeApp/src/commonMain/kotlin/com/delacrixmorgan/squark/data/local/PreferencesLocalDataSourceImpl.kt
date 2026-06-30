package com.delacrixmorgan.squark.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.delacrixmorgan.squark.domain.local.PreferencesLocalDataSource
import com.delacrixmorgan.squark.domain.model.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class PreferencesLocalDataSourceImpl : PreferencesLocalDataSource, KoinComponent {
    companion object {
        const val KEY_THEME = "NENoBgFXBjq3mfLxAEqf"
        const val KEY_BASE_CURRENCY = "pojME2MWx3tqTRx8Ko74"
        const val KEY_QUOTE_CURRENCY = "KgPtesUvb72rF9ZDPzdU"
        const val KEY_MULTIPLIER = "Lq9XeWcN3RkVoBsHaYmZ"
        const val KEY_REDUCE_BOUNCINESS = "Wd4yTpQ7kLm2nXvHsRcB"

        const val DEFAULT_BASE_CURRENCY = "USD"
        const val DEFAULT_QUOTE_CURRENCY = "MYR"
        const val DEFAULT_MULTIPLIER = 1.0
        const val DEFAULT_REDUCE_BOUNCINESS = false
    }

    private val dataStore: DataStore<Preferences> by inject(qualifier = named(LocalDataStore.Preferences.name))

    override suspend fun saveTheme(value: ThemePreference) {
        dataStore.edit { it[stringPreferencesKey(KEY_THEME)] = value.name }
    }

    override suspend fun saveBaseCurrency(value: String) {
        dataStore.edit { it[stringPreferencesKey(KEY_BASE_CURRENCY)] = value }
    }

    override suspend fun saveQuoteCurrency(value: String) {
        dataStore.edit { it[stringPreferencesKey(KEY_QUOTE_CURRENCY)] = value }
    }

    override suspend fun saveMultiplier(value: Double) {
        dataStore.edit { it[doublePreferencesKey(KEY_MULTIPLIER)] = value }
    }

    override suspend fun saveReduceBounciness(value: Boolean) {
        dataStore.edit { it[booleanPreferencesKey(KEY_REDUCE_BOUNCINESS)] = value }
    }

    override fun getTheme(): Flow<ThemePreference> =
        dataStore.data.map {
            ThemePreference.valueOf(it[stringPreferencesKey(KEY_THEME)] ?: ThemePreference.Default.name)
        }

    override fun getBaseCurrency(): Flow<String> =
        dataStore.data.map {
            it[stringPreferencesKey(KEY_BASE_CURRENCY)] ?: DEFAULT_BASE_CURRENCY
        }

    override fun getQuoteCurrency(): Flow<String> =
        dataStore.data.map {
            it[stringPreferencesKey(KEY_QUOTE_CURRENCY)] ?: DEFAULT_QUOTE_CURRENCY
        }

    override fun getMultiplier(): Flow<Double> =
        dataStore.data.map {
            it[doublePreferencesKey(KEY_MULTIPLIER)] ?: DEFAULT_MULTIPLIER
        }

    override fun getReduceBounciness(): Flow<Boolean> =
        dataStore.data.map {
            it[booleanPreferencesKey(KEY_REDUCE_BOUNCINESS)] ?: DEFAULT_REDUCE_BOUNCINESS
        }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}