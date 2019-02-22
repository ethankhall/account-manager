package io.ehdev.account.web.endpoints

import io.ehdev.account.model.user.AccountPrincipal
import io.ehdev.account.web.endpoints.api.UserEndpoint
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class RootEndpoint(private val providers: List<String>, private val baseUrl: String) {

    fun getRoot(request: ServerRequest): Mono<ServerResponse> {
        val user = request.principal().block()
        if (user is AccountPrincipal) {
            val userResponse = UserEndpoint.buildUserResponse(request, user)
            if (userResponse != null) {
                val token = userResponse.authToken.replace(".", ".<wbr>")
                return ServerResponse.ok().render("logged-in-index",
                        mapOf("user" to userResponse.user, "token" to token, "baseUrl" to baseUrl))
            }
        }

        val redirectTo = request.queryParam("redirectTo").orElse(baseUrl)

        return ServerResponse.ok().render("not-logged-in-index", mapOf("providers" to providers, "redirectTo" to redirectTo, "baseUrl" to baseUrl))
    }
}