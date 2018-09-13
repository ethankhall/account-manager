package io.ehdev.account.web.filters

import io.ehdev.account.database.api.UserManager
import io.ehdev.account.model.user.AccountPrincipal
import io.ehdev.account.web.auth.jwt.JwtManager
import io.ehdev.account.web.auth.jwt.JwtTokenAuthentication
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.security.Principal

class JwtUserAuthFilter(private val jwtManager: JwtManager, private val userManager: UserManager) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

        val jwtValue = jwtValue(exchange)

        if (jwtValue != null) {
            val user = findUser(jwtValue)
            if (user != null) {
                val nextExchange = exchange.mutate().principal(Mono.just(user)).build()
                return chain.filter(nextExchange)
            }
        }

        return chain.filter(exchange)
    }

    private fun findUser(jwtValue: String): Principal? {
        val tokenPart = jwtManager.parseToken(jwtValue) ?: return null
        val token = tokenPart as JwtTokenAuthentication.UserJwtTokenAuthentication
        val user = userManager.findUserDetails(token.userId) ?: return null
        return AccountPrincipal(user)
    }

    private fun jwtValue(exchange: ServerWebExchange): String? {
        val headerValue = exchange.request.headers.getFirst(HeaderConst.AUTH_HEADER_NAME)
        val cookieValue = exchange.request.cookies.getFirst(HeaderConst.COOKIE_NAME)?.value

        return headerValue ?: cookieValue
    }

}