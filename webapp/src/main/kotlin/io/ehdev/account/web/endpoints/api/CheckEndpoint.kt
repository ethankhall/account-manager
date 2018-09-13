package io.ehdev.account.web.endpoints.api

import io.ehdev.account.database.api.AccessManager
import io.ehdev.account.database.api.TargetManager
import io.ehdev.account.web.endpoints.api.internal.toValueOrThrow
import io.ehdev.account.web.endpoints.api.internal.verifyLoggedIn
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

class CheckEndpoint(private val targetManager: TargetManager, private val accessManager: AccessManager) {

    companion object {
        private val DEFAULT_ERROR = ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied or Not Found")
    }

    fun checkPermission(request: ServerRequest): Mono<ServerResponse> {
        val accountPrincipal = request.verifyLoggedIn()
        val subjectName = request.pathVariable("subject")
        val resource = request.pathVariable("resource")
        val action = request.pathVariable("action")

        val result = targetManager.getTarget(subjectName).toValueOrThrow(DEFAULT_ERROR)
        val rule = result.ruleContainer.findRule(resource, action) ?: throw DEFAULT_ERROR

        if (!accessManager.hasPermission(accountPrincipal.accountManagerUser, rule)) {
            throw DEFAULT_ERROR
        } else {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .build()
        }
    }
}