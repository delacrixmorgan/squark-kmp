package com.delacrixmorgan.squark.data.remote

import com.delacrixmorgan.squark.data.remote.dto.CurrencyLiveResponseDto
import com.delacrixmorgan.squark.domain.model.result.Result
import com.delacrixmorgan.squark.domain.model.result.exception.DataError.RemoteError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CancellationException

class CurrencyRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : CurrencyRemoteDataSource {

    override suspend fun getLiveRates(): Result<CurrencyLiveResponseDto, RemoteError> = try {
        val response: CurrencyLiveResponseDto = httpClient.get("currency_data/live?source=usd").body()
        if (response.success) {
            Result.success(response)
        } else {
            Result.error(RemoteError(message = "API returned success=false"))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.error(RemoteError(message = e.message, cause = e))
    }
}
