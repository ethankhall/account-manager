package io.ehdev.account.web

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication

open class AccountApplication {
    companion object {
        private val log = LoggerFactory.getLogger(AccountApplication::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(ContainerConfiguration::class.java, *args)
        }
    }
}