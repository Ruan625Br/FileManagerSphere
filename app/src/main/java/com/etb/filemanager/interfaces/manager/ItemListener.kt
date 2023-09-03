/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ItemListener.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.interfaces.manager

import com.etb.filemanager.manager.category.adapter.Category
import java.nio.file.Path

interface ItemListener {

    fun openFileCategory(path: Path, category: Category)
    fun refreshItem()
    fun openItemWith(path: Path)

}