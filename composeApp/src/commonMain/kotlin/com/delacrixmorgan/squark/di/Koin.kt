package com.delacrixmorgan.squark.di

import com.delacrixmorgan.squark.data.local.CurrencyCacheLocalDataSource
import com.delacrixmorgan.squark.data.local.CurrencyCacheLocalDataSourceImpl
import com.delacrixmorgan.squark.data.local.PreferencesLocalDataSourceImpl
import com.delacrixmorgan.squark.data.mapper.CurrencyMapper
import com.delacrixmorgan.squark.data.remote.CurrencyRemoteDataSource
import com.delacrixmorgan.squark.data.remote.CurrencyRemoteDataSourceImpl
import com.delacrixmorgan.squark.data.remote.HttpClientFactory
import com.delacrixmorgan.squark.data.repository.CurrencyRepositoryImpl
import com.delacrixmorgan.squark.data.repository.PreferencesRepositoryImpl
import com.delacrixmorgan.squark.analytics.UserPropertyReporter
import com.delacrixmorgan.squark.data.utils.repository.DataAccessStrategyImpl
import com.delacrixmorgan.squark.domain.local.PreferencesLocalDataSource
import com.delacrixmorgan.squark.domain.repository.CurrencyRepository
import com.delacrixmorgan.squark.domain.repository.PreferencesRepository
import com.delacrixmorgan.squark.domain.usecase.GetConversionRateUseCase
import com.delacrixmorgan.squark.domain.usecase.GetCurrenciesUseCase
import com.delacrixmorgan.squark.domain.usecase.GetRatesUpdatedAtUseCase
import com.delacrixmorgan.squark.platformModule
import com.delacrixmorgan.squark.ui.preference.PreferenceViewModel
import com.delacrixmorgan.squark.ui.preference.currency.CurrencyViewModel
import com.delacrixmorgan.squark.ui.preference.settings.SettingsViewModel
import com.delacrixmorgan.squark.ui.preference.settings.appinfo.AppInfoViewModel
import com.delacrixmorgan.squark.ui.table.TableViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            platformModule(),
            viewModelModule(),
            serviceModule(),
            repositoryModule(),
            useCaseModule(),
            mapperModule(),
            analyticsModule()
        )
    }

fun viewModelModule() = module {
    viewModel { TableViewModel(get(), get(), get()) }
    viewModel { PreferenceViewModel(get()) }
    viewModel { CurrencyViewModel(get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { AppInfoViewModel() }
}

fun serviceModule() = module {
    single { HttpClientFactory.create() }
    single<CurrencyRemoteDataSource> { CurrencyRemoteDataSourceImpl(get()) }
}

fun repositoryModule() = module {
    single { DataAccessStrategyImpl() }
    single<CurrencyCacheLocalDataSource> { CurrencyCacheLocalDataSourceImpl() }
    single<CurrencyRepository> { CurrencyRepositoryImpl(get(), get(), get(), get()) }
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
    single<PreferencesLocalDataSource> { PreferencesLocalDataSourceImpl() }
}

fun useCaseModule() = module {
    single { GetConversionRateUseCase() }
    single { GetCurrenciesUseCase(get()) }
    single { GetRatesUpdatedAtUseCase(get()) }
}

fun mapperModule() = module {
    single { CurrencyMapper() }
}

fun analyticsModule() = module {
    single { UserPropertyReporter(get(), get(), get()) }
}