/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FileAdapterListener.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.interfaces.manager

import com.etb.filemanager.manager.adapter.FileModel

interface FileAdapterListener {

    fun onLongClickListener(item: FileModel, isActionMode: Boolean)

    fun onItemClick(item: FileModel, path: String, isDirectory: Boolean)
}