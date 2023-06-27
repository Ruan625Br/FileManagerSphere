package com.etb.filemanager.settings.preference

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.etb.filemanager.R

class AppearancePreferences : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_appearance, rootKey)
    }
}