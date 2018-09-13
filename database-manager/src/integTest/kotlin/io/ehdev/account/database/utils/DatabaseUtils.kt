package io.ehdev.account.database.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ehdev.account.db.Tables
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.Settings
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext

fun inCleanDb(body: (DSLContext) -> Unit) {
    val dbUrl = "jdbc:mysql://localhost:3306/account_manager?verifyServerCertificate=false&useSSL=true"

    val config = HikariConfig()
    config.jdbcUrl = dbUrl
    config.username = "root"
    config.password = "password"

    val datasource = HikariDataSource(config)

    val jooqConfig = DefaultConfiguration()
            .derive(DataSourceConnectionProvider(datasource))
            .derive(SQLDialect.MYSQL)
            .derive(Settings())

    val context = DefaultDSLContext(jooqConfig)

    listOf(Tables.RULE_GRANT, Tables.SS_USERCONNECTION, Tables.TARGET,
            Tables.TARGET_ACCESS_RULE, Tables.USER_DETAILS, Tables.USER_TOKENS)
            .forEach {
                context.deleteFrom(it).execute()
            }

    context.transaction { it ->
        body.invoke(it.dsl())
    }
}