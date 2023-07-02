package com.etb.filemanager.settings.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.etb.filemanager.R
import com.etb.filemanager.manager.util.MaterialDialogUtils
import com.etb.filemanager.util.file.style.StyleManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class BehaviorPreferences : PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_behavior, rootKey)
        preferenceManager.preferenceDataStore = SettingsDataStore()


        val prefDefaulFolder = findPreference<Preference>("default_folder")
        val currentDefaultFolder = Preferences.Behavior.getDefaultFolder()


        prefDefaulFolder!!.summary = currentDefaultFolder
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.layout_basic_dialog, null)
        val eInputLayout = dialogView.findViewById<TextInputLayout>(R.id.eInputLayout)
        val eInputEditText = dialogView.findViewById<TextInputEditText>(R.id.eInputEditText)

        val title = requireContext().getString(R.string.pref_behavior_set_default_folder_title)
        prefDefaulFolder.setOnPreferenceClickListener { preference ->
            val parent = dialogView.parent as? ViewGroup
            parent?.removeView(dialogView) // Remover a view do pai atual
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
                    Preferences.Behavior.setDefaultFolder(enteredText)
                }

            }.setNegativeButton(R.string.dialog_cancel) { _, _ ->
            }.show()
            true

        }


    }
}