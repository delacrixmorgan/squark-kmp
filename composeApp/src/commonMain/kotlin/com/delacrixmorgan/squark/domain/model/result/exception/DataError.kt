package com.delacrixmorgan.squark.domain.model.result.exception

/**
 * Errors surfaced through the data access strategy. [RemoteError] and [LocalError]
 * are themselves usable as the typed error of a [com.delacrixmorgan.squark.domain.model.result.Result],
 * and both widen to [DataError] at the repository boundary.
 */
sealed class DataError {
    abstract val cause: Throwable?

    data class RemoteError(
        val message: String? = null,
        override val cause: Throwable? = null,
    ) : DataError()

    data class LocalError(
        val message: String? = null,
        override val cause: Throwable? = null,
    ) : DataError()

    data class GenericDataError(
        override val cause: Throwable? = null,
    ) : DataError()
}
