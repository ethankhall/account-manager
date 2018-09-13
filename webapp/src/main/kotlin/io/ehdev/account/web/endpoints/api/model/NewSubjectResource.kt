package io.ehdev.account.web.endpoints.api.model

import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

class NewSubjectResource(
        @Size(min = 5, max = 128) @Pattern(regexp = "[a-zA-Z]([a-zA-Z\\-_0-9]){4,}") val subjectName: String
)