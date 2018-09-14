package io.ehdev.account.model.user

import java.security.Principal

class AccountPrincipal(val accountManagerUser: AccountManagerUser) : Principal {
    override fun getName(): String = accountManagerUser.name
}