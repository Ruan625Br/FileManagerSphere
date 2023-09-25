/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FileItemKeyProvider.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.selection

import androidx.recyclerview.selection.ItemKeyProvider
import com.etb.filemanager.manager.adapter.FileModel

class FileItemKeyProvider(private val fileModels: List<FileModel>) : ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(position: Int): Long? {
        return fileModels[position].id
    }

    override fun getPosition(key: Long): Int {
        return fileModels.indexOfFirst { it.id == key }
    }
}