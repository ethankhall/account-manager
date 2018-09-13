package io.ehdev.account.web.endpoints.api

import io.ehdev.account.model.user.AccountManagerUser
import io.ehdev.account.model.user.AccountPrincipal
import io.ehdev.account.model.user.UserWithToken
import io.ehdev.account.web.endpoints.api.internal.verifyLoggedIn
import io.ehdev.account.web.filters.HeaderConst
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class UserEndpoint {
    fun getUserDetails(request: ServerRequest): Mono<ServerResponse> {
        val user = request.verifyLoggedIn()

        val userResponse = buildUserResponse(request, user)
        return if (userResponse != null) {
            ServerResponse.ok().body(Mono.just(userResponse), UserWithToken::class.java)
        } else {
            ServerResponse.notFound().build()
        }
    }

    companion object {
        fun buildUserResponse(request: ServerRequest, user: AccountPrincipal): UserWithToken? {
            val cookieValue = request.cookies().getFirst(HeaderConst.COOKIE_NAME)?.value
            val headerValue = request.headers().header(HeaderConst.AUTH_HEADER_NAME).firstOrNull()

            return when {
                headerValue != null -> UserWithToken(user.accountManagerUser, headerValue)
                cookieValue != null -> UserWithToken(user.accountManagerUser, cookieValue)
                user.accountManagerUser.userType == AccountManagerUser.UserType.SuperAdmin ->
                    UserWithToken(user.accountManagerUser, "Special: ADMIN!")
                else -> null
            }
        }
    }
}