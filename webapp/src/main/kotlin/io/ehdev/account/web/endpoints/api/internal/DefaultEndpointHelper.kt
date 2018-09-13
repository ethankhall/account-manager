package io.ehdev.account.web.endpoints.api.internal

import io.ehdev.account.database.api.AccessManager
import io.ehdev.account.database.api.TargetManager
import io.ehdev.account.model.ADMIN_RULE
import io.ehdev.account.model.PERMISSION_ADMIN
import io.ehdev.account.model.RESOURCE_MANAGEMENT_NAME
import io.ehdev.account.model.resource.TargetModel
import io.ehdev.account.model.user.AccountManagerUser
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class DefaultEndpointHelper(private val targetManager: TargetManager, private val accessManager: AccessManager) : EndpointHelper {

    override fun verifyUserHasAdminRuleAccess(subjectName: String, user: AccountManagerUser): TargetModel {
        return verifyUserPermission(subjectName, user, RESOURCE_MANAGEMENT_NAME, ADMIN_RULE)
    }

    private fun verifyUserPermission(subjectName: String, user: AccountManagerUser, resource: String, permission: String): TargetModel {
        val target = targetManager.getTarget(subjectName).toValueOrThrowForMissing("Resource $subjectName not found.")
        val adminRole = target.ruleContainer.findRule(resource, permission)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error")

        if (!accessManager.hasPermission(user, adminRole)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "You do not have permission")
        }

        return target
    }

    override fun verifyUserHasAdminPermissionAccess(subjectName: String, user: AccountManagerUser): TargetModel {
        return verifyUserPermission(subjectName, user, RESOURCE_MANAGEMENT_NAME, PERMISSION_ADMIN)
    }
}