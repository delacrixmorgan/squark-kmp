package com.delacrixmorgan.squark.crash

/**
 * Platform-agnostic crash reporting abstraction. Shared code depends on this interface; the binding is
 * provided per-platform via [com.delacrixmorgan.squark.platformModule] (GitLive Firebase Crashlytics on
 * Android, a no-op on iOS until iOS Firebase is configured).
 */
interface CrashReporter {
    /** Adds a breadcrumb message to the next crash/non-fatal report. */
    fun log(message: String)

    /** Records a non-fatal exception. */
    fun recordException(throwable: Throwable)

    /** Sets a custom key/value attached to subsequent reports. */
    fun setCustomKey(key: String, value: String)

    /** Associates subsequent reports with a user identifier. */
    fun setUserId(id: String)
}
