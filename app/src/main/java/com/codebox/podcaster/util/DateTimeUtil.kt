package com.codebox.podcaster.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Codebox on 01/03/21
 */
class DateTimeUtil @Inject constructor() {


    enum class DateTimePattern(val value: String) {
        PATTERN_1("dd_MM_yyyy__HH_mm_ss")
    }

    fun getTimeStamp(pattern: DateTimePattern): String {
        val date = Date(System.currentTimeMillis())
        val formatter: DateFormat = SimpleDateFormat(pattern.value, Locale.getDefault())
        return formatter.format(date)
    }

    fun formatMillis(millis: Long): String {
        var milliseconds = millis
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        val hs = formatTimeString(hours, false, true)
        val ms = formatTimeString(minutes, true, true)
        val ss = formatTimeString(seconds, true, false)

        return "$hs$ms$ss"
    }


    private fun formatTimeString(value: Long, addInitialZeros: Boolean, addColon: Boolean): String {

        val builder = StringBuilder()

        if (value > 0) {
            if (value < 10) {
                builder.append("0")
            }
            builder.append(value)
        } else {
            if (addInitialZeros)
                builder.append("00")
        }

        if (builder.isNotEmpty() && addColon) {
            builder.append(":")
        }
        return builder.toString()

    }
}