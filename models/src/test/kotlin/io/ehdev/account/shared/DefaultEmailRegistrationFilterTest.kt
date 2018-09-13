package io.ehdev.account.shared

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

internal class DefaultEmailRegistrationFilterTest {
    @Test
    fun `filtering specific emails`() {
        val filter = DefaultEmailRegistrationFilter(listOf("ethankhall@pm.me"))

        assertTrue(filter.isEmailAcceptable("ethankhall@pm.me"))
        assertFalse(filter.isEmailAcceptable("ethan@ehdev.io"))
    }

    @Test
    fun `with regexes`() {
        val filter = DefaultEmailRegistrationFilter(listOf(".*@pm.me", ".*@gmail.com"))

        assertTrue(filter.isEmailAcceptable("ethankhall@pm.me"))
        assertTrue(filter.isEmailAcceptable("example@gmail.com"))
        assertFalse(filter.isEmailAcceptable("ethan@ehdev.io"))
    }

    @Test
    fun `with bag regex, will fail on load`() {
        assertThrows(PatternSyntaxException::class.java) {
            DefaultEmailRegistrationFilter(listOf("*.foo"))
        }
    }
}