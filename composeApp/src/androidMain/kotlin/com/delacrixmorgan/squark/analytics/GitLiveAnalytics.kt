package com.delacrixmorgan.squark.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics

/**
 * Android [Analytics] backed by Firebase Analytics via the GitLive Firebase Kotlin SDK.
 * Firebase auto-initializes on Android through the firebase-common ContentProvider (configured by the
 * google-services Gradle plugin), so no manual initialization is required here.
 */
class GitLiveAnalytics : Analytics {
    private val analytics = Firebase.analytics

    override fun setUserProperty(name: String, value: String) =
        analytics.setUserProperty(name, value)
}
