package io.ehdev.account.web.endpoints.api.internal

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.apis.GitHubApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth20Service

class GithubOAuthHelper(
    private val clientId: String,
    private val clientSecret: String,
    objectMapper: ObjectMapper,
    baseUrl: String
) : AbstractOauthHelper(objectMapper, baseUrl) {

    override fun parseUserDetails(tree: JsonNode): OAuthUserDetails {
        val name = tree["name"]
        val email = tree["email"]

        return OAuthUserDetails(name.asText(), listOf(email.asText()))
    }

    override fun createNewService(): OAuth20Service {
        return ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .callback(callbackUri().toString())
                .build(GitHubApi.instance())
    }

    override fun getProtectedResourceUrl(): String = PROTECTED_RESOURCE_URL

    override fun pathProviderName(): String = "github"

    companion object {
        private const val PROTECTED_RESOURCE_URL = "https://api.github.com/user"
    }
}