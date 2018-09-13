package io.ehdev.account.model

sealed class Result<out T>(val value: T?, val error: ErrorCode?) {

    fun isOk() = value != null && error == null
    fun isErr() = value == null && error != null

    companion object {
        fun <T> ok(value: T): Result<T> = Ok(value)
        fun <T> err(errorCode: ErrorCode): Result<T> = Err(errorCode)
    }

    class Ok<T> internal constructor(value: T) : Result<T>(value, null)
    class Err<T> internal constructor(err: ErrorCode) : Result<T>(null, err)
}