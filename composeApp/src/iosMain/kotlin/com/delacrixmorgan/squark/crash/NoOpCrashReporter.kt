package com.delacrixmorgan.squark.crash

/**
 * iOS placeholder [CrashReporter]. Crash reporting on iOS is deferred until the Firebase Apple SDK is
 * added to the Xcode project (via SPM) along with Touchlab CrashKiOS to bridge Kotlin/Native crashes.
 * Swap this for a GitLive-backed implementation at that point.
 */
class NoOpCrashReporter : CrashReporter {
    override fun log(message: String) = Unit
    override fun recordException(throwable: Throwable) = Unit
    override fun setCustomKey(key: String, value: String) = Unit
    override fun setUserId(id: String) = Unit
}
