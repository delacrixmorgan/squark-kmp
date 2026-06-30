package com.delacrixmorgan.squark.data.remote

import com.delacrixmorgan.squark.data.remote.dto.CurrencyLiveResponseDto
import com.delacrixmorgan.squark.domain.model.result.Result
import com.delacrixmorgan.squark.domain.model.result.exception.DataError.RemoteError

interface CurrencyRemoteDataSource {
    suspend fun getLiveRates(): Result<CurrencyLiveResponseDto, RemoteError>
}
