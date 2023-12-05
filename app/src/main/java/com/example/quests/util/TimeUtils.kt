package com.example.quests.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

// `this` is UTC milliseconds since epoch, so use UTC offset
fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this)
    .atOffset(ZoneOffset.UTC)
    .toLocalDate()

// String displayed on Add Task screen
fun LocalDate.toFormattedString(
    pattern: String = "EEEE, MMM d, yyyy", // e.g., Thursday, Nov 30, 2023
    locale: Locale = Locale.getDefault()
): String = this.format(DateTimeFormatter.ofPattern(pattern, locale))

/**
 * Combines this date with a [time] to create a LocalDateTime.
 * Same as java.time.LocalDate(LocalTime time) except that this one
 * sets the time to be the end of the day if [time] is null.
 */
fun LocalDate.atNullableTime(time: LocalTime?): LocalDateTime = when (time) {
    null -> this.atTime(LocalTime.MAX)
    else -> this.atTime(time)
}

fun LocalTime.toFormattedString(
    pattern: String = "h:mm a", // e.g., 2:12 AM
    locale: Locale = Locale.getDefault()
): String = this.format(DateTimeFormatter.ofPattern(pattern, locale))

fun LocalTime.isWithinToday(): Boolean = this.isAfter(LocalTime.now())

// `this` is milliseconds since epoch, using system's default zone
fun Long.toLocalDateTime(): LocalDateTime = Instant.ofEpochMilli(this)
    .atZone(ZoneId.systemDefault())
    .toLocalDateTime()

// String displayed on Home screen
fun LocalDateTime.toFormattedString(
    pattern: String? = null,
    locale: Locale = Locale.getDefault()
): String {
    var newPattern = pattern ?: "MMM d"
    if (this.year != LocalDate.now().year) {
        newPattern = newPattern.plus(", yyyy")
    }
    // this.toLocalTime() only keeps precision up to .999,
    // but LocalTime.MAX is .999999999, so switch it's nano with ours
    if (this.toLocalTime() != LocalTime.MAX.withNano(this.nano)) {
        newPattern = newPattern.plus(" h:mm a")
    }
    return this.format(DateTimeFormatter.ofPattern(newPattern, locale))
}

fun LocalDateTime.toEpochMilli(): Long =
    this.atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()