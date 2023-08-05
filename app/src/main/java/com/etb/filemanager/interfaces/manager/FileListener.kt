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
    fun showBottomSheet(file: FileModel)
    fun onClickFileAction(file: FileModel, action: CreateFileAction)


}