package io.ehdev.account.model.resource

data class TargetModel(val id: Long, val name: String, val ruleContainer: AccessRuleContainer) {
    constructor(id: Long, name: String, rules: List<AccessRuleModel>) : this(id, name, AccessRuleContainer(rules))
}