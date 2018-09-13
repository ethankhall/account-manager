package io.ehdev.account.web.configuration

import io.ehdev.account.web.endpoints.api.AuthorizationEndpoints
import io.ehdev.account.web.endpoints.api.CheckEndpoint
import io.ehdev.account.web.endpoints.api.LogoutEndpoint
import io.ehdev.account.web.endpoints.api.OAuthEndpoints
import io.ehdev.account.web.endpoints.api.PermissionEndpoints
import io.ehdev.account.web.endpoints.RootEndpoint
import io.ehdev.account.web.endpoints.api.UserEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.HandlerStrategies
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.toWebHandler
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.WebHandler

@Configuration
@Import(EndpointConfigs::class, WebFilterConfiguration::class)
open class ApplicationRoutesConfiguration {

    @Bean
    open fun webHandler(router: RouterFunction<ServerResponse>): WebHandler = toWebHandler(router, HandlerStrategies.withDefaults())

    @Bean
    open fun mainServer(authorizationEndpoints: AuthorizationEndpoints,
                        checkEndpoint: CheckEndpoint,
                        permissionEndpoints: PermissionEndpoints,
                        userEndpoints: UserEndpoint,
                        oauthEndpoint: OAuthEndpoints,
                        logoutEndpoint: LogoutEndpoint,
                        rootEndpoint: RootEndpoint): RouterFunction<ServerResponse> {
        return router {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("/api/v1/authorization/{subject}", authorizationEndpoints::retrieveSubject)
                DELETE("/api/v1/authorization/{subject}", authorizationEndpoints::deleteSubject)
                POST("/api/v1/authorization", authorizationEndpoints::createSubject)
                GET("/api/v1/check/{subject}/permission/{resource}/{action}", checkEndpoint::checkPermission)
                GET("/logout", logoutEndpoint::logout)
                POST("/api/v1/authorization/{subject}/permission", permissionEndpoints::newPermission)
                GET("/api/v1/authorization/{subject}/permission/{resource}/{action}", permissionEndpoints::retrievePermission)
                DELETE("/api/v1/authorization/{subject}/permission/{resource}/{action}", permissionEndpoints::deleteResource)
                DELETE("/api/v1/authorization/{subject}/permission/{resource}/{action}/user/{email}", permissionEndpoints::deletePermission)
                PUT("/api/v1/authorization/{subject}/permission/{resource}/{action}/user/{email}", permissionEndpoints::addUser)
                GET("/api/v1/user", userEndpoints::getUserDetails)
                GET("/oauth/{provider}", oauthEndpoint::getToken)
                GET("/oauth/{provider}/callback", oauthEndpoint::callback)
            }
            accept(MediaType.TEXT_HTML).nest {
                GET("/", rootEndpoint::getRoot)
            }
        }
    }
}