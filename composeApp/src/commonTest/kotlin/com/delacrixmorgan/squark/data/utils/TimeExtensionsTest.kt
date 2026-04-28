package com.delacrixmorgan.squark.data.utils

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

class TimeExtensionsTest {

    private val now = Clock.System.now()

    @Test
    fun `instant just under the window is still fresh`() {
        val instant = now - 1439.minutes
        assertTrue(instant.isLessThanMinutesAgo(CACHE_EXPIRATION_24_HOURS_IN_MINUTES, now = now))
    }

    @Test
    fun `instant just over the window is expired`() {
        val instant = now - 1441.minutes
        assertFalse(instant.isLessThanMinutesAgo(CACHE_EXPIRATION_24_HOURS_IN_MINUTES, now = now))
    }

    @Test
    fun `instant exactly at the window boundary is expired`() {
        val instant = now - CACHE_EXPIRATION_24_HOURS_IN_MINUTES.minutes
        assertFalse(instant.isLessThanMinutesAgo(CACHE_EXPIRATION_24_HOURS_IN_MINUTES, now = now))
    }
}
