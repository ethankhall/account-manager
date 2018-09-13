package io.ehdev.account.database.impl

import io.ehdev.account.database.utils.assertErr
import io.ehdev.account.database.utils.assertOk
import io.ehdev.account.database.utils.inCleanDb
import io.ehdev.account.model.ErrorCode
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AccessManagerIntegrationTest {

    @Test(timeout = 10 * 1000)
    fun `create, add, remove workflow, and repeat`() {
        inCleanDb { dslContext ->
            val targetManager = DefaultTargetManager(dslContext)

            for (i in 1..5) {
                assertOk(targetManager.createTarget("test"))
                assertOk(targetManager.createAccess("test", "resource", "foo"))
                assertOk(targetManager.createAccess("test", "resource", "foo1"))


                var resource = assertOk(targetManager.getTarget("test"))
                assertEquals(4, resource.value!!.ruleContainer.rules.size)
                assertNotNull(resource.value!!.ruleContainer.findRule("resource", "foo")
            )

            assertOk(targetManager.deleteAccess("test", "resource", "foo1"))

            resource = assertOk(targetManager.getTarget("test"))
            assertEquals(3, resource.value!!.ruleContainer.size)
            assertNull(resource.value!!.ruleContainer.findRule("resource", "foo1"))

            assertOk(targetManager.deleteTarget("test"))
        }
    }
}

@Test(timeout = 20 * 1000)
fun `error! delete when not exists`() {
    inCleanDb { dslContext ->
        val targetManager = DefaultTargetManager(dslContext)
        assertErr(targetManager.deleteTarget("test"), ErrorCode.TARGET_NOT_EXIST)
        assertOk(targetManager.createTarget("test"))
        assertErr(targetManager.deleteAccess("test", "test-subject", "test-resource"), ErrorCode.ROLE_NOT_EXIST)
        assertOk(targetManager.deleteTarget("test"))
        assertErr(targetManager.getTarget("test"), ErrorCode.TARGET_NOT_EXIST)
    }
}
}
