package com.delacrixmorgan.squark

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.delacrixmorgan.squark.analytics.Analytics
import com.delacrixmorgan.squark.analytics.GitLiveAnalytics
import com.delacrixmorgan.squark.crash.CrashReporter
import com.delacrixmorgan.squark.crash.GitLiveCrashReporter
import com.delacrixmorgan.squark.data.local.LocalDataStore
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val rateUsStoreLink: String = "https://play.google.com/store/apps/details?id=com.delacrixmorgan.squark"

actual fun platformModule(): Module = module {
    single(named(LocalDataStore.Preferences.name)) { dataStore(get(), LocalDataStore.Preferences.path()) }
    single<CrashReporter> { GitLiveCrashReporter() }
    single<Analytics> { GitLiveAnalytics() }
    single { LocaleHelper(get()) }
}

fun dataStore(context: Context, path: String): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve(path).absolutePath }
)

actual fun getVersionCode(): String {
    return BuildConfig.VERSION_CODE
}

actual fun getVersionName(): String {
    return BuildConfig.VERSION_NAME
}