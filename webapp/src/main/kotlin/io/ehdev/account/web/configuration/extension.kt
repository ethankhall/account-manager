package io.ehdev.account.web.configuration

import org.springframework.web.reactive.function.server.ServerRequest

fun ServerRequest.findScheme(): String {
    return this.headers().header("X-Forwarded-Proto").firstOrNull() ?: (this.uri().scheme ?: "http")
}