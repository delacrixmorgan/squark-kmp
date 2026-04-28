package com.delacrixmorgan.squark.data.utils

/**
 * Minimal multiplatform logger used by the data layer. Kept intentionally tiny to
 * avoid pulling in a logging dependency; swap the body for Kermit/Napier if needed.
 */
object Log {
    var enabled: Boolean = true

    fun v(message: String) {
        if (enabled) println("V/Squark: $message")
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (enabled) println("E/Squark: $message${throwable?.let { " | $it" } ?: ""}")
    }
}
