package com.etb.filemanager.interfaces.manager

import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.manager.file.CreateFileAction
import com.etb.filemanager.manager.files.filelist.FileItemSet

interface FileListener {

    fun clearSelectedFiles()
    fun selectFile(file: FileModel, selected: Boolean)
    fun selectFiles(files: FileItemSet, selected: Boolean)
    fun openFile(file: FileModel)
    fun openFileWith(file: FileModel)
    fun cutFile(file: FileItemSet)
    fun copyFile(file: FileItemSet)
    fun confirmDeleteFile(files: FileItemSet?, fileItem: FileModel?)
    fun showRenameFileDialog(file: FileModel)
    fun extractFile(file: FileModel)
    fun showCreateArchiveDialog(file: FileModel)
    fun shareFile(file: FileModel)
    fun copyPath(file: FileModel)
    fun addBookmark(file: FileModel)
    fun createShortcut(file: FileModel)
    fun showPropertiesDialog(file: FileModel)
    fun showBottomSheet(file: FileModel)
    fun showBottomSheetInstallAPK(file: FileModel)
    fun onClickFileAction(file: FileModel, action: CreateFileAction)


}