/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - PathExt.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.nio.file.Path
import kotlin.io.path.pathString

fun Path.toBitmap(): Bitmap? {
    return BitmapFactory.decodeFile(pathString)
}