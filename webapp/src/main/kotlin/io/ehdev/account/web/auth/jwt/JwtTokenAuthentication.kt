package io.ehdev.account.web.auth.jwt

interface JwtTokenAuthentication {
    data class UserJwtTokenAuthentication(val userId: Long, val userTokenId: Long) : JwtTokenAuthentication
}
