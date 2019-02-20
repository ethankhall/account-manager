package io.ehdev.account.web.endpoints.api.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.apis.GitHubApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import io.mikael.urlbuilder.UrlBuilder
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.net.URI

class GithubOAuthHelper(
    private val clientId: String,
    private val clientSecret: String,
    private val objectMapper: ObjectMapper
) : OAuthBackendHelper {

    override fun buildRedirect(callbackUrl: URI, state: String): URI {
        val service = createNewService(callbackUrl)
        return UrlBuilder.fromString(service.getAuthorizationUrl(state)).toUri()
    }

    override fun authenticate(code: String, callbackUrl: URI, state: String): OAuthUserDetails {
        val service = createNewService(callbackUrl)

        val accessToken = service.getAccessToken(code)
        val request = OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL)
        service.signRequest(accessToken, request)

        val response = service.execute(request)

        if (!response.isSuccessful) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to access GitHub. Try again?")
        }

        val bodyJson = objectMapper.readTree(response.body)
        val name = bodyJson["name"]
        val email = bodyJson["email"]

        return OAuthUserDetails(name.asText(), listOf(email.asText()))
    }

    override fun pathProviderName(): String = "github"

    private fun createNewService(callback: URI): OAuth20Service {
        return ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .callback(callback.toString())
                .build(GitHubApi.instance())
    }

    companion object {
        private const val PROTECTED_RESOURCE_URL = "https://api.github.com/user"
    }
}