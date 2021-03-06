package io.ehdev.account.web.endpoints.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

class NewRoleResource(
    @Size(min = 5, max = 128) @Pattern(regexp = "[a-zA-Z]([a-zA-Z\\-_0-9]){4,}") val resource: String,
    @Size(min = 5, max = 128) @Pattern(regexp = "[a-zA-Z]([a-zA-Z\\-_0-9]){4,}") val action: String,
    @JsonProperty(required = false) val defaultUsers: List<String>?
)