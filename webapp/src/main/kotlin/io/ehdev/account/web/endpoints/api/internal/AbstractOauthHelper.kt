package io.ehdev.account.web.endpoints.api.internal

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import io.ehdev.account.getLogger
import io.mikael.urlbuilder.UrlBuilder
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

abstract class AbstractOauthHelper(private val objectMapper: ObjectMapper, private val baseUrl: String) {

    private val log by getLogger()

    fun buildRedirect(state: String): URI {
        val service = createNewService()
        return UrlBuilder.fromString(service.getAuthorizationUrl(state)).toUri()
    }

    fun authenticate(code: String): OAuthUserDetails {
        val service = createNewService()

        val response = try {
            val accessToken = service.getAccessToken(code)
            val request = OAuthRequest(Verb.GET, getProtectedResourceUrl())
            service.signRequest(accessToken, request)

            service.execute(request)
        } catch (e: Exception) {
            log.error("Issue while authenticating", e)
            throw e
        }

        if (!response.isSuccessful) {
            log.info("Unable to access ${pathProviderName()}! Repose body was `{}`", response.body)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to access ${pathProviderName()}. Try again?")
        }

        val bodyJson = objectMapper.readTree(response.body)

        log.debug("JSON Blob: {}", bodyJson)
        return parseUserDetails(bodyJson)
    }

    fun callbackUri(): URI {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .replaceQuery(null)
                .path("/oauth/${pathProviderName()}/callback")
                .build()
                .toUri()
    }

    abstract fun parseUserDetails(tree: JsonNode): OAuthUserDetails

    abstract fun createNewService(): OAuth20Service

    abstract fun pathProviderName(): String

    abstract fun getProtectedResourceUrl(): String
}