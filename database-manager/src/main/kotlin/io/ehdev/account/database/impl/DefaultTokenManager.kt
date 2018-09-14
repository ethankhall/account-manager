package io.ehdev.account.database.impl

import io.ehdev.account.database.api.TokenManager
import io.ehdev.account.db.Tables
import io.ehdev.account.model.user.AccountManagerUser
import io.ehdev.account.toZonedDateTime
import org.jooq.DSLContext
import java.time.Clock
import java.time.Instant
import java.time.ZonedDateTime
import java.util.UUID

class DefaultTokenManager(private val clock: Clock, private val dslContext: DSLContext) : TokenManager {

    override fun getTokenData(id: String): TokenManager.UderlyingTokenDetails? {

        val userTokens = Tables.USER_TOKENS
        val userToken = dslContext
                .selectFrom(userTokens)
                .where(userTokens.PUBLIC_USER_TOKEN.eq(id))
                .fetchOne()?.into(userTokens)
        if (userToken == null || !isTokenValid(userToken.valid, userToken.expiresAt)) {
            return null
        }
        return TokenManager.UderlyingTokenDetails(userToken.userId, userToken.userTokenId)
    }

    private fun isTokenValid(valid: Boolean, expirationDate: Instant): Boolean {
        return valid && clock.instant().isBefore(expirationDate)
    }

    override fun generateUserToken(user: AccountManagerUser, expirationDate: ZonedDateTime): TokenManager.TokenDetails {
        val userTokens = Tables.USER_TOKENS
        val randomToken = "A" + user.userId.toString() + "-" + UUID.randomUUID().toString()
        val result = dslContext
                .insertInto(userTokens, userTokens.USER_ID, userTokens.CREATED_AT, userTokens.EXPIRES_AT, userTokens.PUBLIC_USER_TOKEN)
                .values(user.userId, clock.instant(), expirationDate.toInstant(), randomToken)
                .returning(userTokens.fields().toList())
                .fetchOne()
                .into(userTokens)

        return TokenManager.TokenDetails(result.userTokenId,
                result.publicUserToken,
                result.createdAt.toZonedDateTime(),
                result.expiresAt.toZonedDateTime(),
                result.valid)
    }

    override fun findTokens(user: AccountManagerUser): List<TokenManager.TokenDetails> {
        val tokens = dslContext
                .selectFrom(Tables.USER_TOKENS)
                .where(Tables.USER_TOKENS.USER_ID.eq(user.userId))
                .and(Tables.USER_TOKENS.VALID.eq(true))
                .and(Tables.USER_TOKENS.EXPIRES_AT.greaterOrEqual(clock.instant()))
                .and(Tables.USER_TOKENS.CREATED_AT.lessOrEqual(clock.instant()))
                .fetch()
                .into(Tables.USER_TOKENS)

        return tokens
                .map {
                    TokenManager.TokenDetails(
                            it.userTokenId,
                            it.publicUserToken,
                            it.createdAt.toZonedDateTime(),
                            it.expiresAt.toZonedDateTime(),
                            it.valid)
                }
                .toList()
    }

    override fun invalidateToken(id: String) {
        dslContext
                .update(Tables.USER_TOKENS)
                .set(Tables.USER_TOKENS.VALID, false)
                .where(Tables.USER_TOKENS.PUBLIC_USER_TOKEN.eq(id))
                .execute()
    }
}
