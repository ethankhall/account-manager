package io.ehdev.account.web

import org.springframework.boot.SpringApplication

open class AccountApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(ContainerConfiguration::class.java, *args)
        }
    }
}