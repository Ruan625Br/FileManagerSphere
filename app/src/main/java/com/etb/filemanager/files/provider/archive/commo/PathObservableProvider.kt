package com.etb.filemanager.files.provider.archive.commo

import java.io.IOException
import java.nio.file.Path

interface PathObservableProvider {
    @Throws(IOException::class)
    fun observe(path: Path, intervalMillis: Long): PathObservable
}