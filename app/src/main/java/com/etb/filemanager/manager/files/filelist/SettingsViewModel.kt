/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - SettingsViewModel.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.files.filelist

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel(context: Context) : ViewModel() {
    private val _settingsState: MutableLiveData<SettingsState> = MutableLiveData()
    val settingsState: LiveData<SettingsState> = _settingsState
    private val sharedPopupSettingsActionSort: SharedPreferences =
        context.getSharedPreferences("sharedPopupSettingsActionSort", Context.MODE_PRIVATE)


    fun setSelectedActionShowHiddenFiles() {
        val showHiddenFiles = getActionShowHiddenFiles()


        val newShowHiddenFiles = !showHiddenFiles
        sharedPopupSettingsActionSort.edit().putBoolean("settings_action_show_hidden_files", newShowHiddenFiles).apply()


        _settingsState.value = SettingsState(newShowHiddenFiles)

        Settings.FILE_LIST_SHOW_HIDDEN_FILES = newShowHiddenFiles
    }

     fun getActionShowHiddenFiles(): Boolean {
        return sharedPopupSettingsActionSort.getBoolean("settings_action_show_hidden_files", false)
    }
}
