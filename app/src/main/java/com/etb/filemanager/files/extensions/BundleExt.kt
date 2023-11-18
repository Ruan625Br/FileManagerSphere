/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - BundleExt.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.extensions

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}
