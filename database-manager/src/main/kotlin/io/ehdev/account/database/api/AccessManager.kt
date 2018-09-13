package io.ehdev.account.database.api

import io.ehdev.account.model.resource.AccessRuleModel
import io.ehdev.account.model.resource.RoleId
import io.ehdev.account.model.resource.UserId
import io.ehdev.account.model.user.AccountManagerUser

interface AccessManager {
    fun grantPermission(user: AccountManagerUser, role: AccessRuleModel)

    fun revokePermission(user: AccountManagerUser, role: AccessRuleModel)

    fun hasPermission(user: AccountManagerUser, role: AccessRuleModel): Boolean

    fun getUsersForRole(role: AccessRuleModel): List<UserId>

    fun getRolesForUser(user: AccountManagerUser): List<RoleId>
}