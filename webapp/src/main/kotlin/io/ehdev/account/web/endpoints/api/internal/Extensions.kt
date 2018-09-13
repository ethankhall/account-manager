package io.ehdev.account.web.endpoints.api.internal

import io.ehdev.account.model.Result
import io.ehdev.account.model.resource.AccessRuleContainer
import io.ehdev.account.model.resource.AccessRuleModel
import io.ehdev.account.model.user.AccountPrincipal
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ResponseStatusException

fun ServerRequest.verifyLoggedIn(): AccountPrincipal {
    val principal = this.principal().block()
    when (principal) {
        is AccountPrincipal -> return principal
        else -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    }
}

fun AccessRuleContainer.getRule(resource: String, action: String): AccessRuleModel {
    return this.findRule(resource, action)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Resource/Action was not found.")
}

fun <T> Result<T>.toValueOrThrowForMissing(message: String): T {
    return toValueOrThrow(message, HttpStatus.NOT_FOUND)
}

fun <T> Result<T>.toValueOrThrow(): T {
    return toValueOrThrow("Error while getting value", HttpStatus.INTERNAL_SERVER_ERROR)
}

fun <T> Result<T>.toValueOrThrow(message: String, status: HttpStatus): T {
    return toValueOrThrow(ResponseStatusException(status, message))
}

fun <T> Result<T>.toValueOrThrow(exception: ResponseStatusException): T {
    when (this) {
        is Result.Ok -> return this.value!!
        is Result.Err -> {
            throw exception
        }
    }
}