package io.ehdev.account.web.configuration

import com.codahale.metrics.MetricRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MetricsConfiguration {

    @Bean
    open fun metricsRegistry(): MetricRegistry {
        return MetricRegistry()
    }
}