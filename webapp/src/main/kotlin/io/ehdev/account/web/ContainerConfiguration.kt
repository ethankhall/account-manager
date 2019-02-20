package io.ehdev.account.web

import io.ehdev.account.web.configuration.ApplicationRoutesConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.SchedulingConfiguration
import org.springframework.web.reactive.config.EnableWebFlux

@Import(
        DataSourceAutoConfiguration::class,
        ApplicationRoutesConfiguration::class,
        SchedulingConfiguration::class
)
@Configuration
@EnableWebFlux
@SpringBootApplication
open class ContainerConfiguration