package io.ehdev.account.web

import io.ehdev.account.web.configuration.ApplicationRoutesConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.SchedulingConfiguration
import org.springframework.session.ReactiveMapSessionRepository
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession
import org.springframework.web.reactive.config.EnableWebFlux
import java.util.concurrent.ConcurrentHashMap

@Import(
        DataSourceAutoConfiguration::class,
        ApplicationRoutesConfiguration::class,
        SchedulingConfiguration::class
)
@Configuration
@EnableWebFlux
@SpringBootApplication
@EnableSpringWebSession
open class ContainerConfiguration {

    @Bean
    open fun reactiveSessionRepository() = ReactiveMapSessionRepository(ConcurrentHashMap(20))
}