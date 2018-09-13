package io.ehdev.account.database.api

import io.ehdev.account.model.user.AccountManagerUser
import java.time.ZonedDateTime

interface TokenManager {

    /**
     * Create a user token for AccountManager
     */
    fun generateUserToken(user: AccountManagerUser, expirationDate: ZonedDateTime): TokenDetails

    /**
     * is token valid
     */
    fun getTokenData(id: String): UderlyingTokenDetails?

    /**
     * Kill a token
     */
    fun invalidateToken(id: String)

    fun findTokens(user: AccountManagerUser): List<TokenDetails>

    data class UderlyingTokenDetails(val linkedId: Long, val privateId: Long)

    data class TokenDetails(val id: Long,
                            val publicId: String,
                            val createDate: ZonedDateTime,
                            val expiresAt: ZonedDateTime,
                            val valid: Boolean)
}
