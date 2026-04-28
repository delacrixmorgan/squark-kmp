package com.delacrixmorgan.squark.data.mapper

import com.delacrixmorgan.squark.data.remote.dto.CurrencyLiveResponseDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CurrencyMapperTest {

    private val mapper = CurrencyMapper()

    @Test
    fun `dtoToModel strips source prefix and resolves names`() {
        val dto = CurrencyLiveResponseDto(
            success = true,
            timestamp = 1781117887,
            source = "USD",
            quotes = mapOf("USDMYR" to 4.0, "USDAUD" to 1.5),
        )

        val model = mapper.dtoToModel(dto)

        // Sorted by code; base currency (USD) is re-injected.
        assertEquals(listOf("AUD", "MYR", "USD"), model.map { it.code })
        val myr = model.first { it.code == "MYR" }
        assertEquals(4.0, myr.rateToUSD)
        assertEquals("Malaysian Ringgit", myr.name)
    }

    @Test
    fun `dtoToModel drops codes without a known name`() {
        val dto = CurrencyLiveResponseDto(
            success = true,
            source = "USD",
            quotes = mapOf("USDMYR" to 4.0, "USDZZZ" to 9.9),
        )

        val model = mapper.dtoToModel(dto)

        // MYR + re-injected USD; ZZZ has no known name and is dropped.
        assertEquals(listOf("MYR", "USD"), model.map { it.code })
        assertNull(model.firstOrNull { it.code == "ZZZ" })
    }

    @Test
    fun `dtoToModel includes base currency at rate 1 even without a USDUSD pair`() {
        val dto = CurrencyLiveResponseDto(
            success = true,
            source = "USD",
            quotes = mapOf("USDMYR" to 4.0, "USDAUD" to 1.5),
        )

        val model = mapper.dtoToModel(dto)

        val usd = model.first { it.code == "USD" }
        assertEquals(1.0, usd.rateToUSD)
        assertEquals("United States Dollar", usd.name)
    }

    @Test
    fun `model round-trips through entity`() {
        val dto = CurrencyLiveResponseDto(
            success = true,
            source = "USD",
            quotes = mapOf("USDMYR" to 4.0, "USDAUD" to 1.5),
        )
        val model = mapper.dtoToModel(dto)

        val entity = mapper.modelToEntity(model)
        val restored = mapper.entityToModel(entity)

        assertEquals(model, restored)
    }
}
