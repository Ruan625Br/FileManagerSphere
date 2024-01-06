/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - BehaviorPreferences.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.settings.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.etb.filemanager.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class BehaviorPreferences : PreferenceFragment(){
    override fun getTitle(): Int {
        return R.string.pref_behavior_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_behavior, rootKey)
        preferenceManager.preferenceDataStore = SettingsDataStore()


        val prefDefaultFolder = findPreference<Preference>("default_folder")
        val currentDefaultFolder = Preferences.Behavior.defaultFolder


        //Default folder
        prefDefaultFolder!!.summary = currentDefaultFolder
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.layout_basic_dialog, null)
        val eInputEditText = dialogView.findViewById<TextInputEditText>(R.id.eInputEditText)

        val title = requireContext().getString(R.string.pref_behavior_set_default_folder_title)
        prefDefaultFolder.setOnPreferenceClickListener { preference ->
            val parent = dialogView.parent as? ViewGroup
            parent?.removeView(dialogView)
            eInputEditText.setText(currentDefaultFolder)

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(dialogView)
                .setCancelable(false)
            .setPositiveButton(getString(R.string.set)) { _, _ ->
                val enteredText = eInputEditText.text.toString()
                val path = File(enteredText)
                if (enteredText != currentDefaultFolder && path.exists()){
                    preference.summary = enteredText
                    Preferences.Behavior.defaultFolder = enteredText
                }

            }.setNegativeButton(R.string.dialog_cancel) { _, _ ->
            }.show()
            true

        }

        //Select file long click

        val switchSelectFileLongClick = findPreference<SwitchPreferenceCompat>("select_file_long_click")
        val selectFileLongClick = Preferences.Behavior.selectFileLongClick

        switchSelectFileLongClick?.isChecked = selectFileLongClick

        //Show fast scroll
        val swShowFastScroll = findPreference<SwitchPreferenceCompat>(getString(R.string.pref_key_show_fast_scroll))
        val isFastScrollEnabled = Preferences.Behavior.isFastScrollEnabled
        swShowFastScroll?.isChecked = isFastScrollEnabled


    }
}

