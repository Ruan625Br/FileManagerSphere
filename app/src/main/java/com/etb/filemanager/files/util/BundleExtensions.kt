/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - BundleExtensions.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.util

import android.os.Bundle
import android.os.Parcelable
import com.etb.filemanager.files.app.appClassLoader



fun <T : Parcelable> Bundle.getParcelableSafe(key: String?): T? {
    classLoader = appClassLoader
    return getParcelable(key)
}