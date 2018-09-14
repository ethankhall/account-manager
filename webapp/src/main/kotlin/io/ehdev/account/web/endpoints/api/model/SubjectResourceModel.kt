package io.ehdev.account.web.endpoints.api.model

import io.ehdev.account.model.resource.TargetModel
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

class SubjectResourceModel(
    @Size(min = 5, max = 128) @Pattern(regexp = "[a-zA-Z]([a-zA-Z\\-_0-9]){4,}") val subject: String,
    val roles: List<RoleModel>
) {
    companion object {
        fun create(model: TargetModel): SubjectResourceModel {
            return SubjectResourceModel(model.name,
                    model.ruleContainer.rules.map { RoleModel(it.resourceName, it.actionName) })
        }
    }
}