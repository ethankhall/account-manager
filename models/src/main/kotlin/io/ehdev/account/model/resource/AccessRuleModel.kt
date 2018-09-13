package io.ehdev.account.model.resource

data class AccessRuleModel(val targetId: Long, val id: Long, val resourceName: String, val actionName: String)
