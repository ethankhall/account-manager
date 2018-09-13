package io.ehdev.account.database.impl

import io.ehdev.account.database.api.UserManager
import io.ehdev.account.database.exception.UserIsNotAllowedToRegisterException
import io.ehdev.account.db.tables.UserDetailsTable
import io.ehdev.account.db.tables.records.UserDetailsRecord
import io.ehdev.account.model.user.AccountManagerUser
import io.ehdev.account.shared.EmailRegistrationFilter
import org.apache.commons.lang3.RandomStringUtils
import org.jooq.DSLContext
import javax.inject.Inject

open class DefaultUserManager @Inject constructor(
        private val dslContext: DSLContext,
        private val emailRegistrationFilter: EmailRegistrationFilter
) : UserManager {

    private val details = UserDetailsTable.USER_DETAILS

    override fun createUser(emailAddresses: List<String>, name: String): AccountManagerUser {

        val userEmail = emailAddresses.filter { emailRegistrationFilter.isEmailAcceptable(it) }
                .firstOrNull() ?: throw UserIsNotAllowedToRegisterException(emailAddresses)

        var result: UserDetailsRecord? = null
        while (result == null) {
            val ref = RandomStringUtils.randomAlphanumeric(8)
            result = dslContext
                    .insertInto(details, details.EMAIL, details.USER_REF, details.DISPLAY_NAME)
                    .values(userEmail, ref, name)
                    .returning()
                    .fetchOne()
        }

        val user = result.into(details)

        return user.toAccountManagerUser()
    }

    override fun findUserDetails(emailAddress: List<String>): AccountManagerUser? {
        val user = dslContext
                .selectFrom(details)
                .where(details.EMAIL.`in`(emailAddress))
                .fetchOne()?.into(details) ?: return null
        return user.toAccountManagerUser()
    }

    override fun findUserDetails(id: Long): AccountManagerUser? {
        val user = dslContext
                .selectFrom(details)
                .where(details.USER_ID.eq(id))
                .fetchOne()?.into(details) ?: return null
        return user.toAccountManagerUser()
    }

    fun UserDetailsRecord.toAccountManagerUser(): AccountManagerUser {
        return AccountManagerUser(this.userId, this.userRef, this.email, this.displayName, AccountManagerUser.UserType.User)
    }
}
