package io.ehdev.account.database.utils

import io.ehdev.account.model.ErrorCode
import io.ehdev.account.model.Result
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun <T> assertOk(result: Result<T>): Result.Ok<T> {
    assertTrue(result.isOk(), "Value ($result) was not Ok type")
    return result as Result.Ok
}

fun <T> assertErr(result: Result<T>): Result.Err<T> {
    assertTrue(result.isErr(), "Value ($result) was not Err type")
    return result as Result.Err
}

fun <T> assertErr(result: Result<T>, errorCode: ErrorCode): Result.Err<T> {
    assertTrue(result.isErr(), "Value ($result) was not Err type")
    assertEquals(result.error, errorCode)
    return result as Result.Err
}