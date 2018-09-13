package io.ehdev.account.web.filters

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.Clock

class SessionFilter(private val clock: Clock) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return exchange.session.map { session ->


            if (!session.isStarted) {
                session.start()
            }

            session.attributes["lastModifiedTime"] = clock.millis()
            if (!session.attributes.containsKey("uniqueId")) {
                session.attributes["uniqueId"] = RandomStringUtils.randomAlphanumeric(10)
            }


            session.save()
        }.flatMap {
            chain.filter(exchange)
        }
    }
}