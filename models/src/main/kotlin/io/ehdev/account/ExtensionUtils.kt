package io.ehdev.account

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun Instant.toZonedDateTime(): ZonedDateTime = ZonedDateTime.ofInstant(this, ZoneOffset.UTC)