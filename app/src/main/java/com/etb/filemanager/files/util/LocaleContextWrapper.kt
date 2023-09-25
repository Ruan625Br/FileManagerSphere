/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - LocaleContextWrapper.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.Log
import com.etb.filemanager.settings.preference.Preferences
import java.util.Locale

class LocaleContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {
        fun wrap(context: Context): ContextWrapper {
            val language = Preferences.Interface.language
            val systemLocale = Locale(getSystemLanguage())
            Log.i("TAGGG", "TAG: $language")
            val mLocale = if (language == LangUtils.LANG_AUTO) systemLocale else createLocale(language)
            val config = context.resources.configuration
            @SuppressLint("ObsoleteSdkInt")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocales(LocaleList(mLocale))
            } else {
                @Suppress("DEPRECATION")
                config.locale = mLocale
            }
            Locale.setDefault(mLocale)
            val wrappedContext = context.createConfigurationContext(config)
            return LocaleContextWrapper(wrappedContext)
        }

        private fun createLocale(language: String): Locale {
            val localeParts = language.split("-")
            return if (localeParts.size == 1) {
                Locale(localeParts[0])
            } else {
                Locale(localeParts[0], localeParts[1])
            }
        }
    }
}

fun getSystemLanguage(): String {
    return Resources.getSystem().configuration.locales[0].language
}
