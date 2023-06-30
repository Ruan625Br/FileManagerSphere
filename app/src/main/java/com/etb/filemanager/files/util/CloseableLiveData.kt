package com.etb.filemanager.files.util

import androidx.lifecycle.LiveData
import java.io.Closeable

abstract class CloseableLiveData<T> : LiveData<T>, Closeable {
    constructor(value: T) : super(value)

    constructor()

    abstract override fun close()
}