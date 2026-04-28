package com.delacrixmorgan.squark.domain.model

/** Bucketed device font scale, reported as an analytics user property. */
enum class FontScale {
    Small,
    Normal,
    Large,
    ExtraLarge,
}

fun Float.toFontScale(): FontScale = when {
    this < 1.0F -> FontScale.Small
    this == 1.0F -> FontScale.Normal
    this <= 1.5F -> FontScale.Large
    else -> FontScale.ExtraLarge
}
