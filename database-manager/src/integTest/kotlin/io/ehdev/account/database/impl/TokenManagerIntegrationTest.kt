package io.ehdev.account.database.impl

import io.ehdev.account.database.utils.inCleanDb
import io.ehdev.account.shared.EmailRegistrationFilter
import org.junit.Test
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TokenManagerIntegrationTest {

    @Test(timeout = 60 * 1000)
    fun `test adding tokens`() {
        inCleanDb { dslContext ->
            val begin = LocalDateTime.of(2018, 7, 26, 18, 28, 12, 0)
            val clock = Clock.fixed(
                    begin.toInstant(ZoneOffset.UTC),
                    ZoneOffset.UTC)

            val filter = object : EmailRegistrationFilter {
                override fun isEmailAcceptable(email: String): Boolean = true
            }

            val userManager = DefaultUserManager(dslContext, filter)
            val user = userManager.createUser("a@x.v", "userName")

            val tokenManager = DefaultTokenManager(clock, dslContext)
            assertSame(0, tokenManager.findTokens(user).size)

            val userToken = tokenManager.generateUserToken(user, begin.plusDays(2).atZone(ZoneOffset.UTC))
            assertTrue(userToken.valid)
            assertEquals(begin.plusDays(2).toEpochSecond(ZoneOffset.UTC), userToken.expiresAt.toEpochSecond())

            assertSame(1, tokenManager.findTokens(user).size)

            assertNotNull(tokenManager.getTokenData(userToken.publicId))
            tokenManager.invalidateToken(userToken.publicId)

            assertSame(0, tokenManager.findTokens(user).size)
        }
    }
}
