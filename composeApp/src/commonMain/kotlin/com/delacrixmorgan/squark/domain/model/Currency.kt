package com.delacrixmorgan.squark.domain.model

data class Currency(
    val code: String,
    val name: String,
    val rateToUSD: Double,
)
