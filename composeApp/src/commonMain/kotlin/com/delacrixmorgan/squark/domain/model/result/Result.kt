package com.delacrixmorgan.squark.domain.model.result

/**
 * A discriminated union that encapsulates a successful outcome with a value of type [S]
 * or a failure with an error of type [E].
 *
 * Mirrors the helper surface used by the data access strategy: [success], [failure],
 * [map], [fold], [getOrNull], [get], plus the [Result.success]/[Result.error] factories.
 */
sealed class Result<out S, out E> {
    data class Success<out S>(val value: S) : Result<S, Nothing>()
    data class Error<out E>(val error: E) : Result<Nothing, E>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    companion object {
        fun <S> success(value: S): Result<S, Nothing> = Success(value)
        fun <E> error(error: E): Result<Nothing, E> = Error(error)
    }
}

/** Returns the success value or throws if this is an [Result.Error]. */
fun <S, E> Result<S, E>.get(): S = when (this) {
    is Result.Success -> value
    is Result.Error -> throw IllegalStateException("Called get() on a Result.Error: $error")
}

/** Returns the success value or `null` if this is an [Result.Error]. */
fun <S, E> Result<S, E>.getOrNull(): S? = when (this) {
    is Result.Success -> value
    is Result.Error -> null
}

/** Returns the error or `null` if this is a [Result.Success]. */
fun <S, E> Result<S, E>.errorOrNull(): E? = when (this) {
    is Result.Success -> null
    is Result.Error -> error
}

/** Runs [block] with the success value when this is a [Result.Success]. */
inline fun <S, E> Result<S, E>.success(block: (S) -> Unit): Result<S, E> {
    if (this is Result.Success) block(value)
    return this
}

/** Runs [block] with the error when this is a [Result.Error]. */
inline fun <S, E> Result<S, E>.failure(block: (E) -> Unit): Result<S, E> {
    if (this is Result.Error) block(error)
    return this
}

/** Maps a success value while preserving the error. */
inline fun <S, E, R> Result<S, E>.map(transform: (S) -> R): Result<R, E> = when (this) {
    is Result.Success -> Result.Success(transform(value))
    is Result.Error -> this
}

/** Folds both branches into a single value. */
inline fun <S, E, R> Result<S, E>.fold(
    success: (S) -> R,
    failure: (E) -> R,
): R = when (this) {
    is Result.Success -> success(value)
    is Result.Error -> failure(error)
}
