package com.etb.filemanager.files.util

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
            val mLocale = if (language == LangUtils.LANG_AUTO) systemLocale else Locale(language)
            val config = context.resources.configuration
            val localeList = LocaleList(mLocale)
            Locale.setDefault(mLocale)
            config.setLocale(mLocale)
            config.setLocales(localeList)
            val wrappedContext  = context.createConfigurationContext(config)
            return LocaleContextWrapper(wrappedContext)
        }
    }
}
fun getSystemLanguage(): String {
    return Resources.getSystem().configuration.locales[0].language
}
