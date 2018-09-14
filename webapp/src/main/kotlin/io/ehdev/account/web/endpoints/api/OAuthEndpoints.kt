package io.ehdev.account.web.endpoints.api

import io.ehdev.account.database.api.UserManager
import io.ehdev.account.getLogger
import io.ehdev.account.web.auth.jwt.JwtManager
import io.ehdev.account.web.endpoints.api.internal.OAuthBackendHelper
import io.ehdev.account.web.filters.HeaderConst.COOKIE_NAME
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.net.URI

class OAuthEndpoints(
    private val providerMap: Map<String, OAuthBackendHelper>,
    private val userManager: UserManager,
    private val jwtManager: JwtManager,
    private val cookieDomain: String
) {

    private val log by getLogger()

    fun getToken(request: ServerRequest): Mono<ServerResponse> {
        val provider = request.pathVariable("provider")
        val providerBackend = providerMap[provider.toLowerCase()] ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        val redirectValue = request.queryParam(REDIRECT_NAME).orElseGet {
            request.uriBuilder().replacePath("/api/v1/user").build().toString()
        }

        return request.session().flatMap {
            it.attributes["redirectUrl"] = redirectValue
            it.save()

            val callbackUri = request.uriBuilder()
                    .replaceQuery(null)
                    .path("/callback")
                    .build()

            val uniqueId = it.attributes["uniqueId"] as String

            log.debug("Redirecting to {}, the callback URL is {}", provider, callbackUri)
            val redirectUri = providerBackend.buildRedirect(callbackUri, uniqueId)
            ServerResponse.seeOther(redirectUri).build()
        }
    }

    fun callback(request: ServerRequest): Mono<ServerResponse> {
        val provider = request.pathVariable("provider")
        val code = request.queryParam("code")
        val state = request.queryParam("state")

        if (!state.isPresent || !code.isPresent) {
            return ServerResponse.badRequest().body(
                    Mono.just(mapOf("error" to "Unable to get OAuth required components")),
                    object : ParameterizedTypeReference<Map<String, String>>() {})
        }

        val providerBackend = providerMap[provider.toLowerCase()] ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        val callbackUri = request.uriBuilder().replaceQuery(null).build()

        return request.session().flatMap {
            val redirectTo = it.attributes["redirectUrl"] as String
            val uniqueId = it.attributes["uniqueId"] as String

            if (uniqueId != state.get()) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "State value not the same")
            }

            val (name, email) = providerBackend.authenticate(code.get(), callbackUri, uniqueId)

            val user = userManager.findUserDetails(email) ?: userManager.createUser(email, name)
            val authToken = jwtManager.createUserToken(user)

            val cookie = ResponseCookie.from(COOKIE_NAME, authToken)
                    .path("/")
                    .domain(cookieDomain)
                    .build()

            ServerResponse.temporaryRedirect(URI.create(redirectTo)).cookie(cookie).build()
        }
    }

    companion object {
        const val REDIRECT_NAME = "redirectTo"
    }
}