package io.ehdev.account.web.configuration

import com.codahale.metrics.MetricRegistry
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.web.reactive.ReactiveManagementContextAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(DataSourceHealthIndicatorAutoConfiguration::class, HealthEndpointAutoConfiguration::class,
        ReactiveManagementContextAutoConfiguration::class, HealthIndicatorAutoConfiguration::class)
open class MetricsConfiguration {

    @Bean
    open fun metricsRegistry(): MetricRegistry {
        return MetricRegistry()
    }
}