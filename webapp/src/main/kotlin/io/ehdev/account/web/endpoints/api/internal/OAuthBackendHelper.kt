package io.ehdev.account.web.endpoints.api.internal

import java.net.URI
import java.net.URL
import java.util.*

interface OAuthBackendHelper {
    fun buildRedirect(callbackUrl: URI, state: String): URI
    fun authenticate(code: String, callbackUrl: URI, state: String): OAuthUserDetails
    fun pathProviderName(): String
}

data class OAuthUserDetails(val name: String, val emails: List<String>)