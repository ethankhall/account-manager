package io.ehdev.account.web.endpoints.api

import io.ehdev.account.database.api.AccessManager
import io.ehdev.account.database.api.TargetManager
import io.ehdev.account.database.api.UserManager
import io.ehdev.account.web.endpoints.api.internal.EndpointHelper
import io.ehdev.account.web.endpoints.api.internal.getRule
import io.ehdev.account.web.endpoints.api.internal.toValueOrThrow
import io.ehdev.account.web.endpoints.api.internal.verifyLoggedIn
import io.ehdev.account.web.endpoints.api.model.NewPermission
import io.ehdev.account.web.endpoints.api.model.NewRoleResource
import io.ehdev.account.web.endpoints.api.model.PermissionResultContainer
import io.ehdev.account.web.endpoints.api.model.UserDescription
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

class PermissionEndpoints(
    private val targetManager: TargetManager,
    private val userManager: UserManager,
    private val accessManager: AccessManager,
    private val endpointHelper: EndpointHelper
) {

    fun addUser(request: ServerRequest): Mono<ServerResponse> {
        val user = request.verifyLoggedIn()

        val subjectName = request.pathVariable("subject")
        val resource = request.pathVariable("resource")
        val action = request.pathVariable("action")
        val email = request.pathVariable("email")

        val target = endpointHelper.verifyUserHasAdminPermissionAccess(subjectName, user.accountManagerUser)
        val targetRole = target.ruleContainer.getRule(resource, action)

        val targetUser = userManager.findUserDetails(email)
        val responseStatus = if (targetUser == null) {
            HttpStatus.NOT_FOUND
        } else {
            accessManager.grantPermission(targetUser, targetRole)
            HttpStatus.OK
        }

        return ServerResponse.status(responseStatus).build()
    }

    fun deletePermission(request: ServerRequest): Mono<ServerResponse> {
        val user = request.verifyLoggedIn()

        val subjectName = request.pathVariable("subject")
        val resource = request.pathVariable("resource")
        val action = request.pathVariable("action")
        val email = request.pathVariable("email")

        val target = endpointHelper.verifyUserHasAdminPermissionAccess(subjectName, user.accountManagerUser)
        val targetRole = target.ruleContainer.getRule(resource, action)

        val targetUser = userManager.findUserDetails(email)
        val responseStatus = if (targetUser == null) {
            HttpStatus.NOT_FOUND
        } else {
            accessManager.revokePermission(targetUser, targetRole)
            HttpStatus.OK
        }

        return ServerResponse.status(responseStatus).build()
    }

    fun deleteResource(request: ServerRequest): Mono<ServerResponse> {
        val user = request.verifyLoggedIn()

        val subjectName = request.pathVariable("subject")
        val resource = request.pathVariable("resource")
        val action = request.pathVariable("action")
        val target = endpointHelper.verifyUserHasAdminPermissionAccess(subjectName, user.accountManagerUser)
        val targetRole = target.ruleContainer.getRule(resource, action)

        val users = accessManager.getUsersForRole(targetRole)
        if (users.isNotEmpty()) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST).build()
        }

        targetManager.deleteAccess(subjectName, resource, action)

        return ServerResponse.ok().build()
    }

    fun retrievePermission(request: ServerRequest): Mono<ServerResponse> {
        val user = request.verifyLoggedIn()
        val subjectName = request.pathVariable("subject")
        val resource = request.pathVariable("resource")
        val action = request.pathVariable("action")
        val target = endpointHelper.verifyUserHasAdminPermissionAccess(subjectName, user.accountManagerUser)
        val targetRole = target.ruleContainer.getRule(resource, action)

        val users = accessManager.getUsersForRole(targetRole)
        val userList = users.map {
            val thisUser = userManager.findUserDetails(it.getUserId())!!
            UserDescription(thisUser.userRef, thisUser.email, thisUser.name)
        }.toList()

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(PermissionResultContainer(userList)), PermissionResultContainer::class.java)
    }

    fun newPermission(request: ServerRequest): Mono<ServerResponse> {
        val user = request.verifyLoggedIn()
        val subjectName = request.pathVariable("subject")
        endpointHelper.verifyUserHasAdminPermissionAccess(subjectName, user.accountManagerUser)

        val newPermission = request.bodyToMono(NewRoleResource::class.java).map { body ->
            if (body.resource.startsWith("_")) {
                throw ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Unable to add new permission with resource starting with _ (underscore)")
            }

            val rule = targetManager.createAccess(subjectName, body.resource, body.action).toValueOrThrow()

            val userMap = body.defaultUsers.orEmpty()
                    .map { it to userManager.findUserDetails(it) }
                    .toMap()

            userMap.values.filterNotNull().forEach {
                accessManager.grantPermission(it, rule)
            }

            val usersAdded = userMap.filter { it.value != null }.map { it.key }
            val usersMissing = userMap.filter { it.value == null }.map { it.key }

            return@map NewPermission(usersAdded, usersMissing)
        }

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(newPermission, NewPermission::class.java)
    }
}