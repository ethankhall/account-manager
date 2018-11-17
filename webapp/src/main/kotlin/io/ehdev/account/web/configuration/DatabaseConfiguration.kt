package io.ehdev.account.web.configuration

import io.ehdev.account.web.configuration.internal.JooqMetricsCollector
import io.micrometer.core.instrument.MeterRegistry
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

@Configuration
open class DatabaseConfiguration {

    @Bean
    open fun dslContext(dataSource: DataSource, metrics: MeterRegistry): DSLContext {
        val configuration = DefaultConfiguration()
        configuration.setDataSource(dataSource)
        configuration.setSQLDialect(SQLDialect.MYSQL)
        configuration.set(JooqMetricsCollector(metrics, TimeUnit.SECONDS.toMillis(30)))
        return DSL.using(configuration)
    }
}