package com.delacrixmorgan.squark

import android.app.Application
import com.delacrixmorgan.squark.di.initKoin
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase auto-initializes via the google-services ContentProvider; enable collection explicitly.
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
        initKoin {
            androidContext(this@App)
            androidLogger()
        }
    }
}