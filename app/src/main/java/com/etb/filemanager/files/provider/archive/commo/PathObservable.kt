package com.etb.filemanager.files.provider.archive.commo

import java.io.Closeable

interface PathObservable : Closeable {
    fun addObserver(observer: () -> Unit)

    fun removeObserver(observer: () -> Unit)
}