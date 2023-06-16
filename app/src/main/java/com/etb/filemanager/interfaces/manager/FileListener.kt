package com.etb.filemanager.interfaces.manager

import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.manager.file.CreateFileAction

interface FileListener {

    fun selectFile(file: FileModel, selected: Boolean)
    fun selectFiles(files: MutableList<FileModel>, selected: Boolean)
    fun openFile(file: FileModel)
    fun openFileWith(file: FileModel)
    fun cutFile(file: FileModel)
    fun copyFile(file: FileModel)
    fun confirmDeleteFile(file: FileModel,  multItems: Boolean)
    fun showRenameFileDialog(file: FileModel)
    fun extractFile(file: FileModel)
    fun showCreateArchiveDialog(file: FileModel)
    fun shareFile(file: FileModel)
    fun copyPath(file: FileModel)
    fun addBookmark(file: FileModel)
    fun createShortcut(file: FileModel)
    fun showPropertiesDialog(file: FileModel)
    fun showBottomSheet(file: FileModel)
    fun onClickFileAction(file: FileModel, action: CreateFileAction)


}