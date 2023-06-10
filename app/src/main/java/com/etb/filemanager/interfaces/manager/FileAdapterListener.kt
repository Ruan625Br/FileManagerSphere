package com.etb.filemanager.interfaces.manager

import com.etb.filemanager.manager.adapter.FileModel

interface FileAdapterListener {

    fun onLongClickListener(item: FileModel, isActionMode: Boolean)

    fun onItemClick(item: FileModel, path: String, isDirectory: Boolean)
}