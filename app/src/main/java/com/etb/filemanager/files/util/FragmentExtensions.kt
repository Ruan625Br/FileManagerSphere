/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FragmentExtensions.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.util

import com.etb.filemanager.files.provider.archive.common.mime.compat.checkSelfPermissionCompat


import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.annotation.ArrayRes
import androidx.annotation.AttrRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.IntegerRes
import androidx.annotation.InterpolatorRes
import androidx.annotation.PluralsRes
import androidx.fragment.app.Fragment


fun Fragment.checkSelfPermission(permission: String) =
    requireContext().checkSelfPermissionCompat(permission)

fun Fragment.finish() = requireActivity().finish()

fun Fragment.getAnimation(@AnimRes id: Int) = requireContext().getAnimation(id)

fun Fragment.getBoolean(@BoolRes id: Int) = requireContext().getBoolean(id)

fun Fragment.getDimension(@DimenRes id: Int) = requireContext().getDimension(id)

fun Fragment.getDimensionPixelOffset(@DimenRes id: Int) =
    requireContext().getDimensionPixelOffset(id)

fun Fragment.getDimensionPixelSize(@DimenRes id: Int) = requireContext().getDimensionPixelSize(id)

fun Fragment.getInteger(@IntegerRes id: Int) = requireContext().getInteger(id)

fun Fragment.getInterpolator(@InterpolatorRes id: Int) = requireContext().getInterpolator(id)

fun Fragment.getQuantityString(@PluralsRes id: Int, quantity: Int): String =
    requireContext().getQuantityString(id, quantity)

fun Fragment.getQuantityString(
    @PluralsRes id: Int,
    quantity: Int,
    vararg formatArgs: Any?
): String = requireContext().getQuantityString(id, quantity, *formatArgs)

fun Fragment.getQuantityText(@PluralsRes id: Int, quantity: Int): CharSequence =
    requireContext().getQuantityText(id, quantity)

fun Fragment.getStringArray(@ArrayRes id: Int) = requireContext().getStringArray(id)

fun Fragment.getTextArray(@ArrayRes id: Int): Array<CharSequence> =
    requireContext().getTextArray(id)

@ColorInt
fun Fragment.getColorByAttr(@AttrRes attr: Int) = requireContext().getColorByAttr(attr)

fun Fragment.getColorStateListByAttr(@AttrRes attr: Int) =
    requireContext().getColorStateListByAttr(attr)

@Dimension
fun Fragment.dpToDimension(@Dimension(unit = Dimension.DP) dp: Float) =
    requireContext().dpToDimension(dp)

@Dimension
fun Fragment.dpToDimension(@Dimension(unit = Dimension.DP) dp: Int) =
    requireContext().dpToDimension(dp)

@Dimension
fun Fragment.dpToDimensionPixelOffset(@Dimension(unit = Dimension.DP) dp: Float) =
    requireContext().dpToDimensionPixelOffset(dp)

@Dimension
fun Fragment.dpToDimensionPixelOffset(@Dimension(unit = Dimension.DP) dp: Int) =
    requireContext().dpToDimensionPixelOffset(dp)

@Dimension
fun Fragment.dpToDimensionPixelSize(@Dimension(unit = Dimension.DP) dp: Float) =
    requireContext().dpToDimensionPixelSize(dp)

@Dimension
fun Fragment.dpToDimensionPixelSize(@Dimension(unit = Dimension.DP) dp: Int) =
    requireContext().dpToDimensionPixelSize(dp)

fun Fragment.setResult(resultCode: Int, resultData: Intent? = null) =
    requireActivity().setResult(resultCode, resultData)

val Fragment.shortAnimTime
    get() = requireContext().shortAnimTime

val Fragment.mediumAnimTime
    get() = requireContext().mediumAnimTime

val Fragment.longAnimTime
    get() = requireContext().longAnimTime



fun Fragment.startActivitySafe(intent: Intent, options: Bundle? = null) {
    try {
        startActivity(intent, options)
    } catch (e: ActivityNotFoundException) {
    }
}

fun Fragment.startActivityForResultSafe(intent: Intent, requestCode: Int, options: Bundle? = null) {
    try {
        startActivityForResult(intent, requestCode, options)
    } catch (e: ActivityNotFoundException) {
    }
}