package io.ehdev.account.web.configuration.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("authentication")
class ConfigAuthenticationModel {
    lateinit var jwtSignature: String
    var fixedAuthToken: String? = null
    lateinit var oauthCreds: List<OauthCredentials>
    lateinit var serviceBaseUrl: String
    lateinit var cookieDomain: String
}

class OauthCredentials {
    lateinit var clientName: String
    lateinit var clientId: String
    lateinit var clientSecret: String
}