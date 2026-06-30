package com.delacrixmorgan.squark.analytics

/**
 * iOS placeholder [Analytics]. Analytics on iOS is deferred until the Firebase Apple SDK is added to
 * the Xcode project (via SPM). Swap this for a GitLive-backed implementation at that point.
 */
class NoOpAnalytics : Analytics {
    override fun setUserProperty(name: String, value: String) = Unit
}
