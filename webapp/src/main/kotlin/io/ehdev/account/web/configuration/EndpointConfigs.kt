package io.ehdev.account.web.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import io.ehdev.account.database.api.AccessManager
import io.ehdev.account.database.api.TargetManager
import io.ehdev.account.database.api.UserManager
import io.ehdev.account.web.auth.jwt.JwtManager
import io.ehdev.account.web.configuration.model.ConfigAuthenticationModel
import io.ehdev.account.web.endpoints.RootEndpoint
import io.ehdev.account.web.endpoints.api.AuthorizationEndpoints
import io.ehdev.account.web.endpoints.api.CheckEndpoint
import io.ehdev.account.web.endpoints.api.LogoutEndpoint
import io.ehdev.account.web.endpoints.api.OAuthEndpoints
import io.ehdev.account.web.endpoints.api.PermissionEndpoints
import io.ehdev.account.web.endpoints.api.UserEndpoint
import io.ehdev.account.web.endpoints.api.internal.DefaultEndpointHelper
import io.ehdev.account.web.endpoints.api.internal.EndpointHelper
import io.ehdev.account.web.endpoints.api.internal.GithubOAuthHelper
import io.ehdev.account.web.endpoints.api.internal.GoogleOAuthHelper
import io.ehdev.account.web.endpoints.api.internal.OneLoginOAuthHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(ManagerConfiguration::class)
open class EndpointConfigs {

    @Bean
    open fun oauthEndpoints(
        authConfig: ConfigAuthenticationModel,
        userManager: UserManager,
        jwtManager: JwtManager,
        om: ObjectMapper
    ): OAuthEndpoints {
        val oauthConfigs = authConfig.oauthCreds.map {
            when (it.clientName.toLowerCase()) {
                "github" -> GithubOAuthHelper(it.clientId, it.clientSecret, om)
                "google" -> GoogleOAuthHelper(it.clientId, it.clientSecret, om)
                "onelogin" -> OneLoginOAuthHelper(it.clientId, it.clientSecret, om)
                else -> throw RuntimeException("Unknown OAuth provider ${it.clientName}")
            }
        }.associate {
            it.pathProviderName() to it
        }

        return OAuthEndpoints(oauthConfigs, userManager, jwtManager, authConfig.cookieDomain)
    }

    @Bean
    open fun authorizationEndpoints(
        targetManager: TargetManager,
        accessManager: AccessManager,
        endpointHelper: EndpointHelper
    ): AuthorizationEndpoints {
        return AuthorizationEndpoints(targetManager, accessManager, endpointHelper)
    }

    @Bean
    @Autowired
    open fun logoutEndpoint(authConfig: ConfigAuthenticationModel): LogoutEndpoint {
        return LogoutEndpoint(authConfig.cookieDomain)
    }

    @Bean
    open fun checkEndpoint(targetManager: TargetManager, accessManager: AccessManager): CheckEndpoint {
        return CheckEndpoint(targetManager, accessManager)
    }

    @Bean
    open fun permissionEndpoints(
        targetManager: TargetManager,
        userManager: UserManager,
        accessManager: AccessManager,
        endpointHelper: EndpointHelper
    ): PermissionEndpoints {
        return PermissionEndpoints(targetManager, userManager, accessManager, endpointHelper)
    }

    @Bean
    open fun userEndpoint(): UserEndpoint = UserEndpoint()

    @Bean
    open fun rootEndpoint(authConfig: ConfigAuthenticationModel): RootEndpoint = RootEndpoint(authConfig.oauthCreds.map { it.clientName.toLowerCase() })

    @Bean
    open fun endpointHelper(targetManager: TargetManager, accessManager: AccessManager): EndpointHelper {
        return DefaultEndpointHelper(targetManager, accessManager)
    }
}