/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - Color.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.util


import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt
import android.graphics.Color as AndroidColor

@JvmInline
value class Color(@ColorInt val value: Int)

fun Int.asColor(): Color = Color(this)

val Color.alpha: Int
    @IntRange(from = 0, to = 255)
    get() = AndroidColor.alpha(value)

fun Color.withAlpha(@IntRange(from = 0, to = 255) alpha: Int): Color =
    ColorUtils.setAlphaComponent(value, alpha).asColor()

fun Color.withModulatedAlpha(@FloatRange(from = 0.0, to = 1.0) alphaModulation: Float): Color {
    val alpha = (alpha * alphaModulation).roundToInt()
    return ((alpha shl 24) or (value and 0x00FFFFFF)).asColor()
}

val Color.red: Int
    @IntRange(from = 0, to = 255)
    get() = AndroidColor.red(value)

val Color.green: Int
    @IntRange(from = 0, to = 255)
    get() = AndroidColor.green(value)

val Color.blue: Int
    @IntRange(from = 0, to = 255)
    get() = AndroidColor.blue(value)

fun Color.compositeOver(background: Color): Color =
    ColorUtils.compositeColors(value, background.value).asColor()