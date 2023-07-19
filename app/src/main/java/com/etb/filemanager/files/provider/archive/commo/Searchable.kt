package com.etb.filemanager.files.provider.archive.commo

import java.io.IOException
import java.nio.file.Path

interface Searchable {
    @Throws(IOException::class)
    fun search(directory: Path, query: String, intervalMillis: Long, listener: (List<Path>) -> Unit)
}