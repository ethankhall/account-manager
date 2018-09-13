package io.ehdev.account.shared

class DefaultEmailRegistrationFilter(filters: List<String>) : EmailRegistrationFilter {

    private val filterList = filters.map { Regex(it) }

    override fun isEmailAcceptable(email: String): Boolean {
        return filterList.any { it.matches(email) }
    }
}