package io.ehdev.account.web.filters

import io.ehdev.account.getLogger
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.scheduling.annotation.Scheduled
import java.util.function.Supplier

class AdminTokenGenerator: Supplier<String> {

    private val log by getLogger()

    private var token: String = createNewToken()

    override fun get(): String = token

    private fun createNewToken(): String {
        val randomToken = RandomStringUtils.randomAlphanumeric(32)
        log.warn("Random auth token is: {}", randomToken)

        return randomToken
    }

    @Scheduled(fixedRate = 300_000)
    fun calculateNewValue() {
        token = createNewToken()
    }
}