package io.ehdev.account.model.resource

class AccessRuleContainer(val rules: List<AccessRuleModel>) {
    private val ruleLookup: Map<String, AccessRuleModel> = rules.map { it.resourceName + ":" + it.actionName to it }.toMap()

    fun findRule(resource: String, permission: String): AccessRuleModel? {
        return ruleLookup["$resource:$permission"]
    }

    val size: Int
        get() = rules.size
}