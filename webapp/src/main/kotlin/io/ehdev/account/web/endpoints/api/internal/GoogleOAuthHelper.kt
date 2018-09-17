package io.ehdev.account.web.endpoints.api.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import io.ehdev.account.getLogger
import io.mikael.urlbuilder.UrlBuilder
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.net.URI

class GoogleOAuthHelper(
    private val clientId: String,
    private val clientSecret: String,
    private val objectMapper: ObjectMapper
) : OAuthBackendHelper {

    private val log by getLogger()

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

        val name = bodyJson["displayName"]
        val emails = bodyJson["emails"].map { it["value"].textValue() }

        return OAuthUserDetails(name.asText(), emails)
    }

    override fun pathProviderName(): String = "google"

    private fun createNewService(callback: URI, state: String): OAuth20Service {
        return GoogleApi20.instance()
                .createService(clientId, clientSecret, callback.toString(), "profile email", null,
                        state, "code", null, null, null)
    }

    companion object {
        private const val PROTECTED_RESOURCE_URL = "https://www.googleapis.com/plus/v1/people/me"
    }
}