package io.ehdev.account.web.endpoints.api.internal

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.oauth.OAuth20Service

class OneLoginOAuthHelper(
    private val clientId: String,
    private val clientSecret: String,
    objectMapper: ObjectMapper,
    baseUrl: String
) : AbstractOauthHelper(objectMapper, baseUrl) {

    private val oneLogin = OneLogin()

    override fun parseUserDetails(tree: JsonNode): OAuthUserDetails {
        val name = tree["name"]
        val emails = tree["email"].textValue()

        return OAuthUserDetails(name.asText(), listOf(emails))
    }

    override fun createNewService(): OAuth20Service {
        return oneLogin
                .createService(clientId, clientSecret, callbackUri().toString(), "openid profile", null, "code", null, null, null)
    }

    override fun getProtectedResourceUrl(): String = PROTECTED_RESOURCE_URL

    companion object {
        private const val PROTECTED_RESOURCE_URL = "https://openid-connect.onelogin.com/oidc/me"
    }

    override fun pathProviderName(): String = "onelogin"

    class OneLogin : DefaultApi20() {
        override fun getAuthorizationBaseUrl(): String {
            return "https://openid-connect.onelogin.com/oidc/auth"
        }

        override fun getAccessTokenEndpoint(): String {
            return "https://openid-connect.onelogin.com/oidc/token"
        }
    }
}