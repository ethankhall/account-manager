package io.ehdev.account.web.endpoints.api.internal

import io.ehdev.account.model.resource.TargetModel
import io.ehdev.account.model.user.AccountManagerUser

interface EndpointHelper {
    fun verifyUserHasAdminRuleAccess(subjectName: String, user: AccountManagerUser): TargetModel
    fun verifyUserHasAdminPermissionAccess(subjectName: String, user: AccountManagerUser): TargetModel
}