package io.ehdev.account.database.api

import io.ehdev.account.model.user.AccountManagerUser

interface UserManager {

    fun createUser(emailAddresses: List<String>, name: String): AccountManagerUser

    fun createUser(emailAddresses: String, name: String): AccountManagerUser {
        return createUser(listOf(emailAddresses), name)
    }

    fun findUserDetails(id: Long): AccountManagerUser?

    fun findUserDetails(emailAddress: String): AccountManagerUser? {
        return findUserDetails(listOf(emailAddress))
    }

    fun findUserDetails(emailAddress: List<String>): AccountManagerUser?
}
