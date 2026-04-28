package com.delacrixmorgan.squark.crash

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics

/**
 * Android [CrashReporter] backed by Firebase Crashlytics via the GitLive Firebase Kotlin SDK.
 * Firebase auto-initializes on Android through the firebase-common ContentProvider (configured by the
 * google-services Gradle plugin), so no manual initialization is required here.
 */
class GitLiveCrashReporter : CrashReporter {
    private val crashlytics = Firebase.crashlytics

    override fun log(message: String) = crashlytics.log(message)

    override fun recordException(throwable: Throwable) = crashlytics.recordException(throwable)

    override fun setCustomKey(key: String, value: String) = crashlytics.setCustomKey(key, value)

    override fun setUserId(id: String) = crashlytics.setUserId(id)
}
