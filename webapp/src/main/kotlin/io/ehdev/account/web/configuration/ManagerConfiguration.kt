package io.ehdev.account.web.configuration

import io.ehdev.account.database.api.AccessManager
import io.ehdev.account.database.api.TargetManager
import io.ehdev.account.database.api.TokenManager
import io.ehdev.account.database.api.UserManager
import io.ehdev.account.database.impl.DefaultAccessManager
import io.ehdev.account.database.impl.DefaultTargetManager
import io.ehdev.account.database.impl.DefaultTokenManager
import io.ehdev.account.database.impl.DefaultUserManager
import io.ehdev.account.shared.DefaultEmailRegistrationFilter
import io.ehdev.account.shared.EmailRegistrationFilter
import io.ehdev.account.web.auth.jwt.DefaultJwtManager
import io.ehdev.account.web.auth.jwt.JwtManager
import io.ehdev.account.web.configuration.model.ConfigAuthenticationModel
import io.ehdev.account.web.configuration.model.ConfigAuthorizationModel
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.time.Clock

@Configuration
@Import(DatabaseConfiguration::class)
open class ManagerConfiguration {

    @Bean
    open fun clock(): Clock = Clock.systemUTC()

    @Bean
    @Autowired
    open fun emailRegistrationFilter(config: ConfigAuthorizationModel): EmailRegistrationFilter {
        return DefaultEmailRegistrationFilter(config.allowedEmailRegex)
    }

    @Bean
    open fun userManager(dslContext: DSLContext, emailRegistrationFilter: EmailRegistrationFilter): UserManager {
        return DefaultUserManager(dslContext, emailRegistrationFilter)
    }

    @Bean
    open fun tokenManager(clock: Clock, dslContext: DSLContext): TokenManager {
        return DefaultTokenManager(clock, dslContext)
    }

    @Bean
    @Autowired
    open fun jwtManager(clock: Clock, auth: ConfigAuthenticationModel, tokenManager: TokenManager): JwtManager {
        return DefaultJwtManager(clock, auth.jwtSignature, tokenManager)
    }

    @Bean
    open fun targetManager(dslContext: DSLContext): TargetManager = DefaultTargetManager(dslContext)

    @Bean
    open fun accessManager(dslContext: DSLContext): AccessManager = DefaultAccessManager(dslContext)
}