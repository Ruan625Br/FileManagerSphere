/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - SettingsListener.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.interfaces.settings

import android.content.SharedPreferences

interface SettingsListener {


    fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String)

}