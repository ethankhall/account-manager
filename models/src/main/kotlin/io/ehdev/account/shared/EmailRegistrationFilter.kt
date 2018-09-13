package io.ehdev.account.shared

interface EmailRegistrationFilter {

    fun isEmailAcceptable(email: String): Boolean
}