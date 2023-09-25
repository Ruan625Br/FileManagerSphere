/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FileModelExt.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.extensions

import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.manager.files.filelist.FileSortOptions
import com.etb.filemanager.settings.preference.Preferences


fun List<FileModel>.sortFileModel(): List<FileModel> {
    val fileSortOptions = FileSortOptions(
        Preferences.Popup.sortBy,
        Preferences.Popup.orderFiles,
        Preferences.Popup.isDirectoriesFirst
    )
    return this.sortedWith(fileSortOptions.createComparator())
}
