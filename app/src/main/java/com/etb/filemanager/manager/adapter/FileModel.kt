package com.etb.filemanager.manager.adapter

import android.os.Parcelable
import androidx.annotation.WorkerThread
import com.etb.filemanager.files.util.ParcelableParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import java.io.File
import java.nio.file.Path
import java.util.*

@Parcelize
class FileModel(
    var id: Long, var fileName: String, var filePath: @WriteWith<ParcelableParceler> String, var isDirectory: Boolean, var fileExtension: String, var fileSize: Long,
    var file: File,
    var isSelected: Boolean = false

) : Parcelable

@WorkerThread
fun Path.loadFileItem(): FileModel {
    val file = File(this.toString())
    val fileName = file.name
    val filePath = this.toString()
    val isDirectory = file.isDirectory
    val fileExtension = file.extension
    val fileSize = file.length()

    return FileModel(  UUID.randomUUID().mostSignificantBits, fileName, filePath, isDirectory, fileExtension, fileSize, file)
}