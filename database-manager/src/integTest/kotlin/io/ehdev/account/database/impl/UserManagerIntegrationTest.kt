package io.ehdev.account.database.impl

import io.ehdev.account.database.utils.inCleanDb
import io.ehdev.account.shared.EmailRegistrationFilter
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserManagerIntegrationTest {

    @Test(timeout = 60 * 1000)
    fun `test can add user`() {
        inCleanDb { dslContext ->
            val filter = object: EmailRegistrationFilter {
                override fun isEmailAcceptable(email: String): Boolean = true
            }
            val userManager = DefaultUserManager(dslContext, filter)
            assertNull(userManager.findUserDetails("a@x.v"))

            val user = userManager.createUser("a@x.v", "userName")

            assertEquals(user, userManager.findUserDetails(user.email))
            assertEquals(user, userManager.findUserDetails(user.userId))

            userManager.createUser("a2@x.v", "userName1")
        }
    }
}
