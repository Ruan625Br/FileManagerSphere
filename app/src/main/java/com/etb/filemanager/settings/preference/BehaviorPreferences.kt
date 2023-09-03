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
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.etb.filemanager.R
import com.etb.filemanager.manager.util.MaterialDialogUtils
import com.etb.filemanager.ui.style.StyleManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class BehaviorPreferences : PreferenceFragment(){
    override fun getTitle(): Int {
        return R.string.pref_behavior_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_behavior, rootKey)
        preferenceManager.preferenceDataStore = SettingsDataStore()


        val prefDefaulFolder = findPreference<Preference>("default_folder")
        val currentDefaultFolder = Preferences.Behavior.defaultFolder


        //Default folder
        prefDefaulFolder!!.summary = currentDefaultFolder
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.layout_basic_dialog, null)
        val eInputLayout = dialogView.findViewById<TextInputLayout>(R.id.eInputLayout)
        val eInputEditText = dialogView.findViewById<TextInputEditText>(R.id.eInputEditText)

        val title = requireContext().getString(R.string.pref_behavior_set_default_folder_title)
        prefDefaulFolder.setOnPreferenceClickListener { preference ->
            val parent = dialogView.parent as? ViewGroup
            parent?.removeView(dialogView)
            eInputEditText.setText(currentDefaultFolder)

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(dialogView)
                .setCancelable(false)
            .setPositiveButton(getString(R.string.set)) { dialog, which ->
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

