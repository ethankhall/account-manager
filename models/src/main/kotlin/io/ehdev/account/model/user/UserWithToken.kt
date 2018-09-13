package io.ehdev.account.model.user

data class UserWithToken(val user: AccountManagerUser, val authToken: String)