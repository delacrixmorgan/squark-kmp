package com.delacrixmorgan.squark.data.remote

import com.delacrixmorgan.squark.BuildConfig
import com.delacrixmorgan.squark.data.utils.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Builds the shared Ktor [HttpClient]. The engine is auto-selected per platform
 * (OkHttp on Android, Darwin on iOS) since exactly one engine is on each classpath.
 */
object HttpClientFactory {
    fun create(): HttpClient = HttpClient {
        expectSuccess = true

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                },
            )
        }

        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) = Log.v(message)
            }
        }

        defaultRequest {
            url(BuildConfig.CURRENCY_BASE_URL)
            header("apikey", BuildConfig.SQUARK_API_KEY)
        }
    }
}
