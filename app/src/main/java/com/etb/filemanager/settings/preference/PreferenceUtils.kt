package com.etb.filemanager.settings.preference

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtils(private var context: Context) {

    private val sharedPreferenceUser: SharedPreferences =
        context.getSharedPreferences("sharedSettingsUser", Context.MODE_PRIVATE)

    var newUser: Boolean
        get() = sharedPreferenceUser.getBoolean("settings_is_new_user", true)
        set(value) = sharedPreferenceUser.edit().putBoolean("settings_is_new_user", value).apply()

    fun isNewUser(): Boolean {
        return newUser
    }


}