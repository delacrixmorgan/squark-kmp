package com.delacrixmorgan.squark.domain.repository

import com.delacrixmorgan.squark.domain.model.ThemePreference
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun saveTheme(value: ThemePreference)
    suspend fun saveBaseCurrency(value: String)
    suspend fun saveQuoteCurrency(value: String)
    suspend fun saveMultiplier(value: Double)
    suspend fun saveReduceBounciness(value: Boolean)

    fun getTheme(): Flow<ThemePreference>
    fun getBaseCurrency(): Flow<String>
    fun getQuoteCurrency(): Flow<String>
    fun getMultiplier(): Flow<Double>
    fun getReduceBounciness(): Flow<Boolean>

    suspend fun clear()
}