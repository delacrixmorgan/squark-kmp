package com.delacrixmorgan.squark

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.delacrixmorgan.squark.analytics.Analytics
import com.delacrixmorgan.squark.analytics.NoOpAnalytics
import com.delacrixmorgan.squark.crash.CrashReporter
import com.delacrixmorgan.squark.crash.NoOpCrashReporter
import com.delacrixmorgan.squark.data.local.LocalDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual val rateUsStoreLink: String = "https://play.google.com/store/apps/details?id=com.delacrixmorgan.squark"

actual fun platformModule(): Module = module {
    single(named(LocalDataStore.Preferences.name)) { dataStore(LocalDataStore.Preferences.path()) }
    single<CrashReporter> { NoOpCrashReporter() }
    single<Analytics> { NoOpAnalytics() }
    single { LocaleHelper() }
}

@OptIn(ExperimentalForeignApi::class)
fun dataStore(path: String): DataStore<Preferences> = createDataStore(
    producePath = {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        requireNotNull(documentDirectory).path + "/$path"
    }
)

actual fun getVersionCode(): String {
    return NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String ?: "Unknown"
}

actual fun getVersionName(): String {
    return NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "Unknown"
}