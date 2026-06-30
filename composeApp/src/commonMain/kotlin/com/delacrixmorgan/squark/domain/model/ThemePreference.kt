package com.delacrixmorgan.squark.domain.model

enum class ThemePreference {
    System,
    Light,
    Dark;

    companion object {
        val Default = System
    }

    val label: String
        get() = when (this) {
            System -> "System"
            Light -> "Light"
            Dark -> "Dark"
        }
}