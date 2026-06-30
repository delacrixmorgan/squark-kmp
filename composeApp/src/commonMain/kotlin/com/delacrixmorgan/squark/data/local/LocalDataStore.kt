package com.delacrixmorgan.squark.data.local

enum class LocalDataStore(private val url: String) {
    Preferences("squark.preferences_data_store");

    fun path() = "$url.preferences_pb"
}