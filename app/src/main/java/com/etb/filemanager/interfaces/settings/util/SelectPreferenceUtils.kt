/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - SelectPreferenceUtils.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.interfaces.settings.util

import com.etb.filemanager.interfaces.settings.PopupSettingsListener

class SelectPreferenceUtils {
    private lateinit var listener: PopupSettingsListener

    fun addItemSelectedOnListener(itemSelected: Int, itemSelectedFolderFirst: Boolean) {

        listener.onItemSelectedActionSort(itemSelected, itemSelectedFolderFirst)

    }
    companion object {
        private var instance: SelectPreferenceUtils? = null

        fun getInstance(): SelectPreferenceUtils {
            if (instance == null) {
                instance = SelectPreferenceUtils()
            }
            return instance!!
        }
    }

}