package com.etb.filemanager.manager.selection

import androidx.recyclerview.selection.ItemKeyProvider
import com.etb.filemanager.manager.adapter.FileModel

class FileItemKeyProvider(private val fileModels: List<FileModel>) :
    ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(position: Int): Long {
        return fileModels[position].id
    }

    override fun getPosition(key: Long): Int {
        return fileModels.indexOfFirst { it.id == key }
    }
}