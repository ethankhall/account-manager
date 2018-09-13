package io.ehdev.account.web.configuration

import io.ehdev.account.database.api.UserManager
import io.ehdev.account.web.auth.jwt.JwtManager
import io.ehdev.account.web.filters.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.web.reactive.config.ViewResolverRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer
import org.springframework.web.server.WebFilter
import java.time.Clock
import java.util.function.Supplier


@Configuration
@Import(ManagerConfiguration::class)
open class WebFilterConfiguration : WebFluxConfigurer {

    @Bean
    open fun jwtUserAuthFilter(jwtManager: JwtManager, userManager: UserManager) = JwtUserAuthFilter(jwtManager, userManager)

    @Bean
    open fun adminTokenGenerator() = AdminTokenGenerator()

    @Bean
    open fun definedAdminUserFilter(env: Environment): WebFilter {
        val token = env.getProperty("authentication.fixedAuthToken")

        return when (token) {
            is String -> HeaderAdminFilter(Supplier { token })
            else -> NoopFilter()
        }
    }

    @Bean
    open fun changingTokenAdminFilter(generator: AdminTokenGenerator): WebFilter {
        return HeaderAdminFilter(generator)
    }

    @Bean
    open fun freeMarkerConfig(applicationContext: ApplicationContext,
                              @Value("\${spring.template.path}") templatePath: String?): FreeMarkerConfigurer {
        val configurer = FreeMarkerConfigurer()
        configurer.setPreferFileSystemAccess(templatePath != null)
        configurer.setTemplateLoaderPath(templatePath ?: "classpath:/templates/")
        configurer.setResourceLoader(applicationContext)
        return configurer
    }

    @Bean
    open fun sessionFilter(clock: Clock) = SessionFilter(clock)

    override fun configureViewResolvers(registry: ViewResolverRegistry) {
        registry.freeMarker()
    }
}