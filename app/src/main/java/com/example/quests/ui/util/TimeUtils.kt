package com.example.quests.ui.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Functions taken from https://stackoverflow.com/a/51394768
 */
fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

/**
 * Given [time] in UTC time, we negate the effects of the [offset]
 * to get the time in another time zone (by default, the default
 * TimeZone for the JVM).
 * E.g., if we had 12 AM UTC and wanted 12 AM in PST, we would negate
 * the offset of PST (PST is UTC-08:00 so we would add 8 hours).
 */
fun offsetUTCToLocalTime(
    time: Long,
    offset: Int = TimeZone.getDefault().rawOffset
): Long = time - offset.toLong()