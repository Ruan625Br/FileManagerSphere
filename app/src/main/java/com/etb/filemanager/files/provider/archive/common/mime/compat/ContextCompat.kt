/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ContextCompat.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.provider.archive.common.mime.compat

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.TintTypedArray
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun Context.checkSelfPermissionCompat(permission: String): Int =
    ContextCompat.checkSelfPermission(this, permission)

@ColorInt
fun Context.getColorCompat(@ColorRes id: Int): Int = getColorStateListCompat(id).defaultColor

fun Context.getColorStateListCompat(@ColorRes id: Int): ColorStateList =
    AppCompatResources.getColorStateList(this, id)!!

fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable =
    AppCompatResources.getDrawable(this, id)!!

@SuppressLint("RestrictedApi")
fun Context.obtainStyledAttributesCompat(
    set: AttributeSet? = null,
    @StyleableRes attrs: IntArray,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
): TintTypedArray =
    TintTypedArray.obtainStyledAttributes(this, set, attrs, defStyleAttr, defStyleRes)

@OptIn(ExperimentalContracts::class)
@SuppressLint("RestrictedApi")
inline fun <R> TintTypedArray.use(block: (TintTypedArray) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return try {
        block(this)
    } finally {
        recycle()
    }
}

val Context.mainExecutorCompat: Executor
    get() = ContextCompat.getMainExecutor(this)

fun <T> Context.getSystemServiceCompat(serviceClass: Class<T>): T =
    ContextCompat.getSystemService(this, serviceClass)!!

