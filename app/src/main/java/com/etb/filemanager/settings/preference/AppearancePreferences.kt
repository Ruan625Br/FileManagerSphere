package com.etb.filemanager.settings.preference

import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import com.etb.filemanager.R
import com.etb.filemanager.util.file.style.StyleManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AppearancePreferences : PreferenceFragmentCompat() {

    private lateinit var mCurrentTheme: String
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_appearance, rootKey)
        preferenceManager.preferenceDataStore = SettingsDataStore()

        val themesEntries = requireContext().resources.getStringArray(R.array.themes_entries)
        val themesValues = requireContext().resources.getStringArray(R.array.themes_values)

        mCurrentTheme = Preferences.Appearance.getAppTheme()
        val mCurrentTheIndex = themesValues.indexOf(mCurrentTheme)

        val animFileList = Preferences.Appearance.isEnabledAnimFileList()
        var appThemeSummary = themesEntries.get(mCurrentTheIndex)



        val appTheme = findPreference<Preference>("app_theme")
            ?: throw IllegalArgumentException("Preference not found: app_theme")

        appTheme.summary = appThemeSummary
        appTheme.setOnPreferenceClickListener { preference ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.themes_title))
                .setSingleChoiceItems(themesEntries, mCurrentTheIndex) { dialog, which ->
                     if (which != mCurrentTheIndex){
                         val theme = StyleManager.OptionStyle.valueOf(themesValues[which])
                         Preferences.Appearance.setAppTheme(theme.name)
                         StyleManager().setTheme(theme, requireContext())
                          restartActivity()
                     }

                    dialog.cancel()
                }

                .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, which ->
                }
                .show()
                        true
        }

/*
        val switchAnimFileList = findPreference<SwitchPreferenceCompat>("switch_anim_file_list")
                 switchAnimFileList?.isChecked = animFileList
        switchAnimFileList?.setOnPreferenceClickListener { checked ->
            Preferences.Appearance.setAnimFileList(switchAnimFileList.isChecked)
            true
        }
*/
    }

    private fun restartActivity() {
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }



}