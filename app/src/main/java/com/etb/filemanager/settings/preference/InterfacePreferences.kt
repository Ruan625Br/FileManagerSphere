package com.etb.filemanager.settings.preference

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.etb.filemanager.R
import com.etb.filemanager.files.util.getStringArray

class InterfacePreferences : PreferenceFragment() {
    override fun getTitle(): Int {
        return R.string.pref_interface_title
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_interface, rootKey)
        preferenceManager.preferenceDataStore = SettingsDataStore()


//        //Anim in file list
//        val swtAnimInFileList = findPreference<SwitchPreferenceCompat>("testssss")
//        val isEnabledAnimInFileList = Preferences.Interface.isAnimationEnabledForFileList
//        swtAnimInFileList?.isChecked = isEnabledAnimInFileList

        //Rounded corners
        val swtRoundedCorners = findPreference<SwitchPreferenceCompat>("rounded_corners")
        val isEnabledRoundedCorner = Preferences.Interface.isEnabledRoundedCorners
        swtRoundedCorners?.isChecked = isEnabledRoundedCorner

        //View File Information
        val simpleMenuPreference = findPreference<ListPreference>(getString(R.string.pref_key_view_file_information))
        val fileInformationEntries = requireContext().resources.getStringArray(R.array.view_file_information_entries)
        val fileInformationValues = requireContext().resources.getStringArray(R.array.view_file_information_values)

        var currentFileInformationOption = Preferences.Interface.viewFileInformationOption
        var mCurrentTheIndex = fileInformationValues.indexOf(currentFileInformationOption.name)
        var fileInformationSummary = fileInformationEntries[mCurrentTheIndex]

        simpleMenuPreference?.summary = fileInformationSummary

        simpleMenuPreference?.setOnPreferenceChangeListener { preference, newValue ->
            val newOptionName = newValue as String
            val newOption = ViewFileInformationOption.valueOf(newOptionName)
            currentFileInformationOption = newOption

            mCurrentTheIndex = fileInformationValues.indexOf(currentFileInformationOption.name)
            fileInformationSummary = fileInformationEntries[mCurrentTheIndex]
            simpleMenuPreference.summary = fileInformationSummary

            true
        }    }

    enum class ViewFileInformationOption {
        DATE_ONLY,
        SIZE_ONLY,
        NAME_ONLY,
        EVERYTHING
    }
}

