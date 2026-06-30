package com.delacrixmorgan.squark.domain.usecase

import com.delacrixmorgan.squark.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

class GetRatesUpdatedAtUseCase(
    private val repository: CurrencyRepository,
) {
    operator fun invoke(): Flow<Instant?> = repository.getRatesUpdatedAt()
}
