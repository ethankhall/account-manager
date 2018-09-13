package io.ehdev.account.web.endpoints.api

import io.ehdev.account.web.filters.HeaderConst
import org.springframework.http.ResponseCookie
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class LogoutEndpoint(private val cookieDomain: String) {
    fun logout(request: ServerRequest): Mono<ServerResponse> {
        val path = request.uriBuilder().replacePath("/").build()

        val cookie = ResponseCookie.from(HeaderConst.COOKIE_NAME, "")
                .domain(cookieDomain)
                .path("/")
                .maxAge(-1)
                .build()

        return ServerResponse.temporaryRedirect(path).cookie(cookie).build()
    }
}