package io.ehdev.account.web.endpoints.api.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import io.ehdev.account.getLogger
import io.mikael.urlbuilder.UrlBuilder
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.net.URI

class OneLoginOAuthHelper(
    private val clientId: String,
    private val clientSecret: String,
    private val objectMapper: ObjectMapper
) : OAuthBackendHelper {

    private val log by getLogger()

    private val oneLogin = OneLogin()

    override fun buildRedirect(callbackUrl: URI, state: String): URI {
        val service = createNewService(callbackUrl, state)
        return UrlBuilder.fromString(service.authorizationUrl).toUri()
    }

    override fun authenticate(code: String, callbackUrl: URI, state: String): OAuthUserDetails {
        val service = createNewService(callbackUrl, state)

        val accessToken = service.getAccessToken(code)
        val request = OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL)
        service.signRequest(accessToken, request)

        val response = service.execute(request)

        if (!response.isSuccessful) {
            log.info("Unable to access Google! Repose body was `{}`", response.body)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to access Google. Try again?")
        }

        val bodyJson = objectMapper.readTree(response.body)

        log.info("JSON Blob: {}", bodyJson)

        val name = bodyJson["name"]
        val emails = bodyJson["email"].textValue()

        return OAuthUserDetails(name.asText(), listOf(emails))
    }

    companion object {
        private const val PROTECTED_RESOURCE_URL = "https://openid-connect.onelogin.com/oidc/me"
    }

    override fun pathProviderName(): String = "onelogin"

    private fun createNewService(callback: URI, state: String): OAuth20Service {
        return oneLogin
                .createService(clientId, clientSecret, callback.toString(), "openid profile", null,
                        state, "code", null, null, null)
    }

    class OneLogin : DefaultApi20() {
        override fun getAuthorizationBaseUrl(): String {
            return "https://openid-connect.onelogin.com/oidc/auth"
        }

        override fun getAccessTokenEndpoint(): String {
            return "https://openid-connect.onelogin.com/oidc/token"
        }

    }
}