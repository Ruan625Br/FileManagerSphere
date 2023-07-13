package com.etb.filemanager.settings.preference

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.etb.filemanager.R

class InterfacePreferences : PreferenceFragmentCompat() {
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

    }
}