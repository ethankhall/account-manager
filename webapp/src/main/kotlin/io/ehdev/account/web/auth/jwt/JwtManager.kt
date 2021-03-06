package io.ehdev.account.web.auth.jwt

import io.ehdev.account.database.api.TokenManager
import io.ehdev.account.model.user.AccountManagerUser

interface JwtManager {

    fun createUserToken(user: AccountManagerUser): String

    fun createToken(tokenDetails: TokenManager.TokenDetails): String

    fun parseToken(token: String?): JwtTokenAuthentication?

    fun createHandshakeToken(values: Map<String, String>): String

    fun parseHandshakeToken(token: String?, keys: List<String>): Map<String, String>?
}
