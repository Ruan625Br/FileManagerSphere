package com.etb.filemanager.manager.adapter

import android.os.Parcelable
import com.etb.filemanager.files.util.ParcelableParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import java.io.File
import java.nio.file.Path

@Parcelize
class FileModel(
    var id: Long, var fileName: String, var filePath: @WriteWith<ParcelableParceler> String, var isDirectory: Boolean, var fileExtension: String, var fileSize: Long,
    var file: File,
    var isSelected: Boolean = false

) : Parcelable