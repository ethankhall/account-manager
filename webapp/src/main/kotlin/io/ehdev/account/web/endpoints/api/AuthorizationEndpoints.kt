package io.ehdev.account.web.endpoints.api

import io.ehdev.account.database.api.AccessManager
import io.ehdev.account.database.api.TargetManager
import io.ehdev.account.web.endpoints.api.internal.EndpointHelper
import io.ehdev.account.web.endpoints.api.internal.toValueOrThrow
import io.ehdev.account.web.endpoints.api.internal.verifyLoggedIn
import io.ehdev.account.web.endpoints.api.model.NewSubjectResource
import io.ehdev.account.web.endpoints.api.model.SubjectResourceModel
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

class AuthorizationEndpoints(private val targetManager: TargetManager,
                             private val accessManager: AccessManager,
                             private val endpointHelper: EndpointHelper) {
    fun retrieveSubject(request: ServerRequest): Mono<ServerResponse> {
        val accountPrincipal = request.verifyLoggedIn()
        val subjectName = request.pathVariable("subject")
        val target = endpointHelper.verifyUserHasAdminRuleAccess(subjectName, accountPrincipal.accountManagerUser)
        return ServerResponse.ok()
                .body(Mono.just(SubjectResourceModel.create(target)), SubjectResourceModel::class.java)
    }

    fun deleteSubject(request: ServerRequest): Mono<ServerResponse> {
        val accountPrincipal = request.verifyLoggedIn()
        val subjectName = request.pathVariable("subject")
        endpointHelper.verifyUserHasAdminRuleAccess(subjectName, accountPrincipal.accountManagerUser)
        targetManager.deleteTarget(subjectName)
        return ServerResponse.ok().build()
    }

    fun createSubject(request: ServerRequest): Mono<ServerResponse> {
        val accountPrincipal = request.verifyLoggedIn()
        val response = request.bodyToMono(NewSubjectResource::class.java).map { body ->
            if (targetManager.hasTarget(body.subjectName)) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "The subject already exists")
            }

            val subject = targetManager.createTarget(body.subjectName).toValueOrThrow()
            subject.ruleContainer.rules.forEach {
                accessManager.grantPermission(accountPrincipal.accountManagerUser, it)
            }
            SubjectResourceModel.create(subject)
        }
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response, SubjectResourceModel::class.java)
    }
}