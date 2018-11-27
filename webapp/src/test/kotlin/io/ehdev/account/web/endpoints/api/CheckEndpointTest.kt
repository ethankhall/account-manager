package io.ehdev.account.web.endpoints.api

import io.ehdev.account.database.api.AccessManager
import io.ehdev.account.database.api.TargetManager
import io.ehdev.account.model.ErrorCode
import io.ehdev.account.model.Result
import io.ehdev.account.model.user.AccountManagerUser
import io.ehdev.account.model.user.AccountPrincipal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import org.springframework.web.reactive.function.server.EntityResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import kotlin.test.assertEquals

internal class CheckEndpointTest {

    @Test
    internal fun willRespondWithFalseIfNothingIsKnown() {
        val targetManager = mock(TargetManager::class.java)
        val accessManager = mock(AccessManager::class.java)
        val endpoint = CheckEndpoint(targetManager, accessManager)

        val request = MockServerRequest.builder().method(HttpMethod.GET)
                .pathVariable("subject", "foo")
                .pathVariable("resource", "bar")
                .queryParam("action", "ADMIN,READ")
                .principal(AccountPrincipal(AccountManagerUser.ADMIN_USER))
                .build()

        `when`(targetManager.getTarget("foo")).thenReturn(Result.err(ErrorCode.TARGET_NOT_EXIST))

        val response = endpoint.checkPermissionList(request).block() as EntityResponse<Mono<Map<String, Boolean>>>

        assertEquals(2, response.entity().block()?.size)
        assertEquals(false, response.entity().block()?.get("ADMIN"))
        assertEquals(false, response.entity().block()?.get("READ"))
    }

    @Test
    internal fun willBeUnauthorizedIfNotLoggedIn() {
        val targetManager = mock(TargetManager::class.java)
        val accessManager = mock(AccessManager::class.java)
        val endpoint = CheckEndpoint(targetManager, accessManager)

        val request = MockServerRequest.builder().method(HttpMethod.GET)
                .pathVariable("subject", "foo")
                .pathVariable("resource", "bar")
                .queryParam("action", "ADMIN")
                .build()

        val exception = assertThrows<ResponseStatusException> { endpoint.checkPermissionList(request).block() }
        assertEquals(HttpStatus.UNAUTHORIZED, exception.status)
    }
}