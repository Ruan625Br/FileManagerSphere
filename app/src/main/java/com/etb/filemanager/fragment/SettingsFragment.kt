package com.etb.filemanager.fragment

import android.os.Bundle
import com.etb.filemanager.R
import com.etb.filemanager.settings.preference.PreferenceFragment
import com.etb.filemanager.settings.preference.SettingsDataStore

class SettingsFragment : PreferenceFragment() {
    override fun getTitle(): Int {
        return R.string.settings
    }

    fun getInstance(key: String?): SettingsFragment {
        val preferences = SettingsFragment()
        val args = Bundle()
        args.putString(PREF_KEY, key)
        preferences.arguments = args
        return preferences
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        preferenceManager.preferenceDataStore = SettingsDataStore()
    }


}


