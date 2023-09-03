/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - CloseableLiveData.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.util

import androidx.lifecycle.LiveData
import java.io.Closeable

abstract class CloseableLiveData<T> : LiveData<T>, Closeable {
    constructor(value: T) : super(value)

    constructor()

    abstract override fun close()
}