package com.example.quests.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()

fun LocalDate.toFormattedString(
    pattern: String = "EEEE, MMM d, yyyy", // e.g., Thursday, Nov 30, 2023
    locale: Locale = Locale.getDefault()
): String = this.format(DateTimeFormatter.ofPattern(pattern, locale))

/**
 * Combines this date with a [time] to create a LocalDateTime.
 * Same as java.time.LocalDate(LocalTime time) except that this one
 * sets the time to be the end of the day if [time] is null.
 */
fun LocalDate.atTime(time: LocalTime?): LocalDateTime = when {
    time == null -> this.atTime(LocalTime.MAX)
    else -> this.atTime(time)
}

fun LocalTime.toFormattedString(
    pattern: String = "h:mm a", // e.g., 2:12 AM
    locale: Locale = Locale.getDefault()
): String = this.format(DateTimeFormatter.ofPattern(pattern, locale))

fun LocalTime.isWithinToday(): Boolean = this.isAfter(LocalTime.now())

fun LocalDateTime.toEpochMilli(): Long =
    this.atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()