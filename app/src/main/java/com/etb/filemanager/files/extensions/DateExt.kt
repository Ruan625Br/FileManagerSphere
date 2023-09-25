/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - DateExt.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.extensions

import android.text.format.DateFormat
import com.etb.filemanager.ui.util.Constants
import java.util.Calendar
import java.util.Locale

fun Long.getDate(
    format: CharSequence = Constants.DEFAULT_DATE_FORMAT,
): String {
    val mediaDate = Calendar.getInstance(Locale.US)
    mediaDate.timeInMillis = this * 1000L
    return DateFormat.format(format, mediaDate).toString()
}

fun Long.getDate(
    format: CharSequence = Constants.DEFAULT_DATE_FORMAT,
    weeklyFormat: CharSequence = Constants.WEEKLY_DATE_FORMAT,
    extendedFormat: CharSequence = Constants.EXTENDED_DATE_FORMAT,
    stringToday: String,
    stringYesterday: String
): String {
    val currentDate = Calendar.getInstance(Locale.US)
    currentDate.timeInMillis = System.currentTimeMillis()
    val mediaDate = Calendar.getInstance(Locale.US)
    mediaDate.timeInMillis = this * 1000L
    val different: Long = System.currentTimeMillis() - mediaDate.timeInMillis
    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24

    val daysDifference = different / daysInMilli

    return when (daysDifference.toInt()) {
        0 -> {
            stringToday
        }

        1 -> {
            stringYesterday
        }

        else -> {
            if (daysDifference.toInt() in 2..5) {
                DateFormat.format(weeklyFormat, mediaDate).toString()
            } else {
                if (currentDate.get(Calendar.YEAR) > mediaDate.get(Calendar.YEAR)) {
                    DateFormat.format(extendedFormat, mediaDate).toString()
                } else DateFormat.format(format, mediaDate).toString()
            }
        }
    }
}