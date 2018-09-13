package io.ehdev.account.database.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class UserIsNotAllowedToRegisterException(emails: List<String>) :
        ResponseStatusException(HttpStatus.FORBIDDEN, "The requested emails ($emails) are not allowed")