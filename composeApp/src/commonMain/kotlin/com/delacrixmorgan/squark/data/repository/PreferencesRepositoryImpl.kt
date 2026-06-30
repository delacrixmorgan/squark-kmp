package com.delacrixmorgan.squark.data.repository

import com.delacrixmorgan.squark.domain.local.PreferencesLocalDataSource
import com.delacrixmorgan.squark.domain.model.ThemePreference
import com.delacrixmorgan.squark.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent

class PreferencesRepositoryImpl(
    private val localDataStore: PreferencesLocalDataSource,
) : PreferencesRepository, KoinComponent {
    override suspend fun saveTheme(value: ThemePreference) {
        localDataStore.saveTheme(value)
    }

    override suspend fun saveBaseCurrency(value: String) {
        localDataStore.saveBaseCurrency(value)
    }

    override suspend fun saveQuoteCurrency(value: String) {
        localDataStore.saveQuoteCurrency(value)
    }

    override suspend fun saveMultiplier(value: Double) {
        localDataStore.saveMultiplier(value)
    }

    override suspend fun saveReduceBounciness(value: Boolean) {
        localDataStore.saveReduceBounciness(value)
    }

    override fun getTheme(): Flow<ThemePreference> =
        localDataStore.getTheme()

    override fun getBaseCurrency(): Flow<String> =
        localDataStore.getBaseCurrency()

    override fun getQuoteCurrency(): Flow<String> =
        localDataStore.getQuoteCurrency()

    override fun getMultiplier(): Flow<Double> =
        localDataStore.getMultiplier()

    override fun getReduceBounciness(): Flow<Boolean> =
        localDataStore.getReduceBounciness()

    override suspend fun clear() {
        localDataStore.clear()
    }
}