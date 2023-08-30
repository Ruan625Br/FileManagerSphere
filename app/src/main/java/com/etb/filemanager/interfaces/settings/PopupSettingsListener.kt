package com.etb.filemanager.interfaces.settings


interface PopupSettingsListener {
    fun onItemSelectedActionSort(itemSelected: Int, itemSelectedFolderFirst: Boolean)
    fun onFileInfoReceived(currentPath: String)
}