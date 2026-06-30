package com.delacrixmorgan.squark.analytics

/**
 * Platform-agnostic analytics abstraction. Shared code depends on this interface; the binding is
 * provided per-platform via [com.delacrixmorgan.squark.platformModule] (GitLive Firebase Analytics on
 * Android, a no-op on iOS until iOS Firebase is configured).
 */
interface Analytics {
    /** Sets a user property used to segment the audience in the analytics console. */
    fun setUserProperty(name: String, value: String)
}
