/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FileModel.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.adapter

import android.os.Parcelable
import androidx.annotation.WorkerThread
import com.etb.filemanager.files.util.ParcelableParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import java.io.File
import java.nio.file.Path
import java.util.*
import kotlin.io.path.*

@Parcelize
class FileModel(
    var id: Long, var fileName: String, var filePath: @WriteWith<ParcelableParceler> String, var isDirectory: Boolean, var fileExtension: String, var fileSize: Long,
    var file: File,
    var isHidden: Boolean

) : Parcelable

@WorkerThread
fun Path.loadFileItem(): FileModel {
    val file = toFile()
    val fileName = name
    val filePath = pathString
    val isDirectory = isDirectory()
    val fileExtension = extension
    val fileSize = fileSize()
    val isHidden = isHidden()

    return FileModel(UUID.randomUUID().mostSignificantBits, fileName, filePath, isDirectory, fileExtension, fileSize, file, isHidden)
}