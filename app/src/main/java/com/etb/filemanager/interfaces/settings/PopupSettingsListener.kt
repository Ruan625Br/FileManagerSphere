/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - PopupSettingsListener.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.interfaces.settings




interface PopupSettingsListener {
     fun onItemSelectedActionSort(itemSelected: Int, itemSelectedFolderFirst: Boolean)
     fun onFileInfoReceived(currentPath: String)
}