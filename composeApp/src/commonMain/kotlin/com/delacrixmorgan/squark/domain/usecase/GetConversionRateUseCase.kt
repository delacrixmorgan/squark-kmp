package com.delacrixmorgan.squark.domain.usecase

import com.delacrixmorgan.squark.domain.model.Currency

class GetConversionRateUseCase {

    operator fun invoke(
        currencies: List<Currency>,
        baseCode: String,
        quoteCode: String,
    ): Double? {
        val baseCurrency = currencies.find { it.code == baseCode } ?: return null
        val quoteCurrency = currencies.find { it.code == quoteCode } ?: return null
        return quoteCurrency.rateToUSD / baseCurrency.rateToUSD
    }
}
