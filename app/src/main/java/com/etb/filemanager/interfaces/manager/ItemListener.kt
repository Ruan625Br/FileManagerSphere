package com.etb.filemanager.interfaces.manager

import com.etb.filemanager.manager.category.adapter.Category
import java.nio.file.Path

interface ItemListener {

    fun openFileCategory(path: Path, category: Category)
    fun refreshItem()
    fun openItemWith(path: Path)

}