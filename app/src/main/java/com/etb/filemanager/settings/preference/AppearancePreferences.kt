package com.etb.filemanager.settings.preference

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.etb.filemanager.R
import com.etb.filemanager.activity.SettingsActivity
import com.etb.filemanager.util.file.style.StyleManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class AppearancePreferences : PreferenceFragment() {

    private lateinit var mCurrentTheme: String


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_appearance, rootKey)
        preferenceManager.preferenceDataStore = SettingsDataStore()

        val themesEntries = requireContext().resources.getStringArray(R.array.themes_entries)
        val themesValues = requireContext().resources.getStringArray(R.array.themes_values)


        val isEnabledDMaterialDesign3 = Preferences.Appearance.isEnabledDMaterialDesign3
        mCurrentTheme = Preferences.Appearance.appTheme
        val mCurrentTheIndex = if (isEnabledDMaterialDesign3)themesValues.indexOf(mCurrentTheme) else 0

        val appThemeSummary = if (isEnabledDMaterialDesign3)themesEntries[mCurrentTheIndex] else
            getString(R.string.material_design_default_theme)


        //App theme
        val appTheme = findPreference<Preference>("app_theme")
            ?: throw IllegalArgumentException("Preference not found: app_theme")
        appTheme.isEnabled = isEnabledDMaterialDesign3
        appTheme.summary = appThemeSummary
        appTheme.setOnPreferenceClickListener { preference ->
            MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.themes_title))
                .setSingleChoiceItems(themesEntries, mCurrentTheIndex) { dialog, which ->
                    if (which != mCurrentTheIndex) {
                        val theme = StyleManager.OptionStyle.valueOf(themesValues[which])
                        Preferences.Appearance.appTheme = theme.name
                        (activity as SettingsActivity).restart()
                    }

                    dialog.cancel()
                }

                .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, which ->
                }.show()
            true
        }

        //Material Design 3
        val sMaterialDesign =
            findPreference<SwitchPreferenceCompat>(getString(R.string.pref_key_material_design_3))
        val isEnabledMaterialDesign = Preferences.Appearance.isEnabledDMaterialDesign3
        sMaterialDesign?.isChecked = isEnabledMaterialDesign

        sMaterialDesign?.setOnPreferenceChangeListener { preference, newValue ->
            val newValueMaterial = newValue as Boolean
            appTheme.isEnabled = newValueMaterial
            val theme = StyleManager.OptionStyle.MATERIAL_DESIGN_TWO.name
            val themeBase = StyleManager.OptionStyle.FOLLOW_SYSTEM.name
            Preferences.Appearance.appTheme = if (newValueMaterial) themeBase else theme
            restart()
            true
        }


    }

    override fun getTitle(): Int {
        return R.string.pref_cat_appearance
    }

    private fun restart() {
        (activity as SettingsActivity).restart()

    }

}



