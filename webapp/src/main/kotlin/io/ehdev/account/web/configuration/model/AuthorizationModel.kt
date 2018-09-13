package io.ehdev.account.web.configuration.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("authorization")
class ConfigAuthorizationModel {
    lateinit var allowedEmailRegex: List<String>
}