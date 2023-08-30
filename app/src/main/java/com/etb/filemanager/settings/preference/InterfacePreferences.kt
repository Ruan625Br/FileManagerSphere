package com.etb.filemanager.settings.preference

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.etb.filemanager.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
        val simpleMenuPreference =
            findPreference<ListPreference>(getString(R.string.pref_key_view_file_information))
        val fileInformationEntries =
            requireContext().resources.getStringArray(R.array.view_file_information_entries)
        val fileInformationValues =
            requireContext().resources.getStringArray(R.array.view_file_information_values)

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
        }

        //Transparent list background
        val sTransparentBackground =
            findPreference<SwitchPreferenceCompat>(getString(R.string.pref_key_transparent_list_background))
        val isEnabledTransBackground = Preferences.Interface.isEnabledTransparentListBackground
        sTransparentBackground?.isChecked = isEnabledTransBackground

        //Background of files in transparent list
        val opacityEntries =
            requireContext().resources.getStringArray(R.array.selected_file_background_opacity_entries)
        val opacityValues =
            requireContext().resources.getStringArray(R.array.selected_file_background_opacity_values)

        val currentOpacity = Preferences.Interface.selectedFileBackgroundOpacity.toString()
        var currentOpacityIndex = opacityValues.indexOf(currentOpacity)

        val opacitySummary = opacityEntries[currentOpacityIndex]

        val selectedFileBackgroundOpacity =
            findPreference<Preference>(getString(R.string.pref_key_selected_file_background_opacity))!!

        selectedFileBackgroundOpacity.summary = opacitySummary
        selectedFileBackgroundOpacity.setOnPreferenceClickListener { preference ->
            MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.pref_title_selected_file_background_opacity))
                .setSingleChoiceItems(opacityEntries, currentOpacityIndex) { dialog, which ->
                    if (which != currentOpacityIndex) {
                        currentOpacityIndex = which
                        val opacity = opacityValues[which]!!
                        Preferences.Interface.selectedFileBackgroundOpacity = opacity.toFloat()
                        selectedFileBackgroundOpacity.summary = opacityEntries[currentOpacityIndex]
                    }

                    dialog.cancel()
                }
                .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, which ->
                }.show()
            true
        }

        //File list margins
        val fileListMarginsEntries =
            requireContext().resources.getStringArray(R.array.file_list_margins_entries)
        val fileListMarginsValues =
            requireContext().resources.getStringArray(R.array.file_list_margins_values)

        val currentMargin = Preferences.Interface.fileListMargins.toString()
        var currentMarginIndex = fileListMarginsValues.indexOf(currentMargin)

        val fileListMarginsSummary = fileListMarginsEntries[currentMarginIndex]

        val fileListMargins =
            findPreference<Preference>(getString(R.string.pref_key_file_list_margins))!!

        fileListMargins.summary = fileListMarginsSummary
        fileListMargins.setOnPreferenceClickListener { preference ->
            MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.pref_title_file_list_margins))
                .setSingleChoiceItems(fileListMarginsEntries, currentMarginIndex) { dialog, which ->
                    if (which != currentMarginIndex) {
                        currentMarginIndex = which
                        val margin = fileListMarginsValues[which].toInt()
                        Preferences.Interface.fileListMargins = margin
                        fileListMargins.summary = fileListMarginsEntries[currentMarginIndex]

                    }

                    dialog.cancel()
                }
                .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, which ->
                }.show()
            true
        }

    }

    enum class ViewFileInformationOption {
        DATE_ONLY,
        SIZE_ONLY,
        NAME_ONLY,
        EVERYTHING
    }
}

