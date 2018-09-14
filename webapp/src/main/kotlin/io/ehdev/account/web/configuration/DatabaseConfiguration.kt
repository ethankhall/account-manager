package io.ehdev.account.web.configuration

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import javax.sql.DataSource

@Configuration
@Import(MetricsConfiguration::class)
open class DatabaseConfiguration {

    @Bean
    open fun dslContext(dataSource: DataSource): DSLContext {
        return DSL.using(dataSource, SQLDialect.MYSQL)
    }
}