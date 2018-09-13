package io.ehdev.account.web.auth.jwt

import io.ehdev.account.database.api.TokenManager
import io.ehdev.account.getLogger
import io.ehdev.account.model.user.AccountManagerUser
import io.ehdev.account.toZonedDateTime
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.spec.SecretKeySpec

class DefaultJwtManager constructor(
        private val clock: Clock,
        signingKey: String,
        private val tokenManager: TokenManager) : JwtManager {

    private val log by getLogger()

    private val key: ByteArray = signingKey.toByteArray()

    override fun createUserToken(user: AccountManagerUser): String {
        val token = tokenManager.generateUserToken(user, clock.instant().plus(30, ChronoUnit.DAYS).toZonedDateTime())
        return createToken(token)
    }

    override fun createToken(tokenDetails: TokenManager.TokenDetails): String {
        val claims = Jwts
                .claims()
                .setSubject(tokenDetails.publicId)
                .setExpiration(Date.from(tokenDetails.expiresAt.toInstant()))
                .setNotBefore(Date.from(tokenDetails.createDate.toInstant()))

        return Jwts
                .builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SecretKeySpec(key, "HmacSHA256"))
                .compact()
    }

    override fun parseToken(token: String?): JwtTokenAuthentication? {
        if (token == null || token.isBlank()) {
            log.debug("Token was blank")
            return null
        }

        try {
            val claimsJws = Jwts
                    .parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)

            val parsed = claimsJws.body

            val tokenId = parsed.subject
            val tokenData = tokenManager.getTokenData(tokenId) ?: return null

            return JwtTokenAuthentication.UserJwtTokenAuthentication(tokenData.linkedId, tokenData.privateId)
        } catch (exception: JwtException) {
            log.debug("Token {} was invalid: {}", token, exception.message)
            return null
        } catch (exception: IllegalArgumentException) {
            log.debug("Token {} was invalid: {}", token, exception.message)
            return null
        }
    }
}
