package com.etb.filemanager.interfaces.manager

import java.nio.file.Path

interface ItemListener {

    fun openFileCategory(path: Path)
    fun refreshItem()
    fun openItemWith(path: Path)

}