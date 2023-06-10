package com.etb.filemanager.manager.adapter

import java.io.File

class FileModel(
    var id: Long, var fileName: String, var filePath: String, var isDirectory: Boolean, var fileExtension: String, var fileSize: Long,
    var file: File
)