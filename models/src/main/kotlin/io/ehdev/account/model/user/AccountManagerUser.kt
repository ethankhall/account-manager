package io.ehdev.account.model.user

data class AccountManagerUser(
    val userId: Long,
    val userRef: String,
    val email: String,
    val name: String,
    val userType: UserType
) {

    enum class UserType {
        User,
        SuperAdmin,
        Anonymous
    }

    companion object {
        val ANONYMOUS = AccountManagerUser(-1L, "anonymous", "unknown", "anonymous", UserType.Anonymous)
        val ADMIN_USER = AccountManagerUser(-1, "amin", "admin@admin.com", "admin", UserType.SuperAdmin)
    }
}