/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - BaseScreen.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.provider

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.etb.filemanager.files.util.LocaleContextWrapper
import com.etb.filemanager.settings.preference.Preferences
import com.etb.filemanager.ui.style.StyleManager

abstract class BaseScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme()
        super.onCreate(savedInstanceState)

    }

    private fun applyTheme() {
        val styleManager = StyleManager()
        val optionStyle = StyleManager.OptionStyle.valueOf(Preferences.Appearance.appTheme)
        styleManager.setTheme(optionStyle, this)
    }

    override fun attachBaseContext(newBase: Context) {
        val context = LocaleContextWrapper.wrap(newBase)
        super.attachBaseContext(context)
    }
}
