package io.ehdev.account.database.api

import io.ehdev.account.model.Result
import io.ehdev.account.model.resource.AccessRuleModel
import io.ehdev.account.model.resource.TargetModel

interface TargetManager {

    fun createTarget(targetName: String): Result<TargetModel>

    fun deleteTarget(targetName: String): Result<Boolean>

    fun getTarget(targetName: String): Result<TargetModel>

    fun createAccess(targetName: String, resource: String, permission: String): Result<AccessRuleModel>

    fun deleteAccess(targetName: String, resource: String, permission: String): Result<Boolean>
    fun hasTarget(targetName: String): Boolean
}