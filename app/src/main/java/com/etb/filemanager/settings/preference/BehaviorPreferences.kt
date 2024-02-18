/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - BehaviorPreferences.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.settings.preference

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.etb.filemanager.R
import com.jn.filepickersphere.filelist.common.mime.MimeType
import com.jn.filepickersphere.filepicker.FilePickerCallbacks
import com.jn.filepickersphere.filepicker.FilePickerSphereManager
import com.jn.filepickersphere.filepicker.style.FilePickerStyle
import com.jn.filepickersphere.models.FileModel
import com.jn.filepickersphere.models.FilePickerModel
import com.jn.filepickersphere.models.PickOptions

class BehaviorPreferences : PreferenceFragment() {
    override fun getTitle(): Int {
        return R.string.pref_behavior_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_behavior, rootKey)
        preferenceManager.preferenceDataStore = SettingsDataStore()


        //Default folder
        val prefDefaultFolder = findPreference<Preference>("default_folder")!!
        val currentDefaultFolder = Preferences.Behavior.defaultFolder

        prefDefaultFolder.summary = currentDefaultFolder
        prefDefaultFolder.setOnPreferenceClickListener { _ ->

            selectFolder {
                Preferences.Behavior.defaultFolder = it
                prefDefaultFolder.summary = it
            }
            true

        }

        //Select file long click
        val switchSelectFileLongClick =
            findPreference<SwitchPreferenceCompat>("select_file_long_click")
        val selectFileLongClick = Preferences.Behavior.selectFileLongClick

        switchSelectFileLongClick?.isChecked = selectFileLongClick

        //Show fast scroll
        val swShowFastScroll =
            findPreference<SwitchPreferenceCompat>(getString(R.string.pref_key_show_fast_scroll))
        val isFastScrollEnabled = Preferences.Behavior.isFastScrollEnabled
        swShowFastScroll?.isChecked = isFastScrollEnabled


    }

    private fun selectFolder(onSelectedFolder: (String) -> Unit) {

        val defaultPath =  requireContext().getString(R.string.default_pref_default_folder)
        val model = FilePickerModel(
            PickOptions(
                mimeType = listOf(MimeType.DIRECTORY),
                localOnly = false,
                maxSelection = null
            )
        )
        FilePickerSphereManager(
            context = requireContext(), filePickerCallbacks = object : FilePickerCallbacks {
                override fun onAllFilesSelected(files: List<FileModel>) {
                    val path = if (files.isEmpty()) defaultPath else files.first().path
                    onSelectedFolder(path)
                }

                override fun onFileSelectionChanged(file: FileModel, selected: Boolean) {

                }

                override fun onOpenFile(file: FileModel) {

                }

                override fun onSelectedFilesChanged(files: List<FileModel>) {

                }

            }, filePickerModel = model
        ).style(
            FilePickerStyle(
                appTheme = Preferences.Appearance.getAppTheme()
            )
        ).picker()
    }

}