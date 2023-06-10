package com.etb.filemanager.interfaces.settings

import android.content.SharedPreferences

interface SettingsListener {


    fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String)

}