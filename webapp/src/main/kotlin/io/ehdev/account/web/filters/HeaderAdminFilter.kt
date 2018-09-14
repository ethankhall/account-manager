package io.ehdev.account.web.filters

import io.ehdev.account.model.user.AccountManagerUser
import io.ehdev.account.model.user.AccountPrincipal
import io.ehdev.account.web.filters.HeaderConst.AUTH_HEADER_NAME
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.function.Supplier

class HeaderAdminFilter(private val supplier: Supplier<String>) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val header = exchange.request.headers.getFirst(AUTH_HEADER_NAME)

        val nextExchange = if (supplier.get() == header) {
            exchange.mutate().principal(Mono.just(AccountPrincipal(AccountManagerUser.ADMIN_USER))).build()
        } else {
            exchange
        }

        return chain.filter(nextExchange)
    }
}