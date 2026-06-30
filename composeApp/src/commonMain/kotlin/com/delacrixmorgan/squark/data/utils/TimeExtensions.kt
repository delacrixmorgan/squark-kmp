package com.delacrixmorgan.squark.data.utils

import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/** 24 hours expressed in minutes — the cache window for currency rates. */
const val CACHE_EXPIRATION_24_HOURS_IN_MINUTES: Int = 24 * 60

/**
 * Returns `true` if this instant is more recent than [minutes] ago (i.e. still fresh).
 */
fun Instant.isLessThanMinutesAgo(
    minutes: Int,
    now: Instant = Clock.System.now(),
): Boolean = now - this < minutes.minutes

/**
 * Formats this instant as a human-readable relative time, e.g. "just now",
 * "5 minutes ago", "2 hours ago", "3 days ago".
 */
fun Instant.toRelativeTimeString(now: Instant = Clock.System.now()): String {
    val elapsed = now - this
    val minutes = elapsed.inWholeMinutes
    val hours = elapsed.inWholeHours
    val days = elapsed.inWholeDays
    return when {
        minutes < 1 -> "just now"
        minutes == 1L -> "1 minute ago"
        hours < 1 -> "$minutes minutes ago"
        hours == 1L -> "1 hour ago"
        days < 1 -> "$hours hours ago"
        days == 1L -> "1 day ago"
        else -> "$days days ago"
    }
}
