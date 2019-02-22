package io.ehdev.account.web.endpoints.api.internal

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.oauth.OAuth20Service

class GoogleOAuthHelper(
    private val clientId: String,
    private val clientSecret: String,
    objectMapper: ObjectMapper,
    baseUrl: String
) : AbstractOauthHelper(objectMapper, baseUrl) {

    override fun parseUserDetails(tree: JsonNode): OAuthUserDetails {
        val name = tree["displayName"]
        val emails = tree["emails"].map { it["value"].textValue() }

        return OAuthUserDetails(name.asText(), emails)
    }

    override fun createNewService(): OAuth20Service {
        return GoogleApi20.instance()
                .createService(clientId, clientSecret, callbackUri().toString(), "profile email", null, "code", null, null, null)
    }

    override fun getProtectedResourceUrl(): String = PROTECTED_RESOURCE_URL

    override fun pathProviderName(): String = "google"

    companion object {
        private const val PROTECTED_RESOURCE_URL = "https://www.googleapis.com/plus/v1/people/me"
    }
}