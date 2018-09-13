package io.ehdev.account.model

enum class ErrorCode(val errorCode: Int, val message: String) {
    TARGET_NOT_EXIST(1, "The subject does not exist."),
    TARGET_ALREADY_EXIST(2, "The subject does not exist."),
    TO_MANY_ACTIONS_DEFINED(3, "No more actions allowed for resource"),
    ROLE_NOT_EXIST(4, "The resource does not exist."),
    UNABLE_TO_ADD_PERMISSION(5, "Unable to add Permission")
}