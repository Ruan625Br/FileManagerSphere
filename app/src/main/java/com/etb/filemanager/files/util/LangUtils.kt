package com.etb.filemanager.files.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.ArrayMap
import androidx.core.os.ConfigurationCompat
import com.etb.filemanager.R
import com.etb.filemanager.settings.preference.Preferences
import java.util.IllformedLocaleException
import java.util.Locale


class LangUtils {
    val LANG_AUTO = "auto"
    val LANG_DEFAULT = "en"

    private  var sLocaleMap: ArrayMap<String, Locale>? = null

    @SuppressLint("AppBundleLocaleChanges") // We don't use Play Store
    private fun loadAppLanguages(context: Context) {
        if (sLocaleMap == null) sLocaleMap = ArrayMap()
        val res = context.resources
        val conf = res.configuration
        val locales = context.resources.getStringArray(R.array.languages_key)
        val appDefaultLocale = Locale.forLanguageTag(LANG_DEFAULT)
        for (locale in locales) {
            conf.setLocale(Locale.forLanguageTag(locale))
            val ctx = context.createConfigurationContext(conf)
            val langTag = ctx.getString(R.string._lang_tag)
            if (LANG_AUTO == locale) {
                sLocaleMap!![LANG_AUTO] = null
            } else if (LANG_DEFAULT == langTag) {
                sLocaleMap!![LANG_DEFAULT] = appDefaultLocale
            } else {
                // Using conf.locale for API levels < 24
                sLocaleMap!![locale] = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    ConfigurationCompat.getLocales(conf)[0]
                } else {
                    conf.locale
                }
            }
        }
    }

    fun getAppLanguages(context: Context): ArrayMap<String, Locale> {
        if (sLocaleMap == null) loadAppLanguages(context)
        return sLocaleMap!!
    }

    fun getFromPreference(context: Context): Locale {
        val language: String = Preferences.Interface.getLanguage(context)
        val locale = getAppLanguages(context)[language]
        if (locale != null) {
            return locale
        }
        // Load from system configuration
        val conf = Resources.getSystem().configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) conf.locales[0] else conf.locale
    }

    fun isValidLocale(languageTag: String): Boolean {
        try {
            val locale = Locale.forLanguageTag(languageTag)
            for (validLocale in Locale.getAvailableLocales()) {
                if (validLocale == locale) {
                    return true
                }
            }
        } catch (ignore: IllformedLocaleException) {
        }
        return false
    }

    fun getSeparatorString(): String {
        return if (Locale.getDefault().language == Locale("fr").language) {
            " : "
        } else ": "
    }

     fun getLanguagesL(locales: ArrayMap<String, Locale>): Array<CharSequence?> {
        val localesL = arrayOfNulls<CharSequence>(locales.size)
        var locale: Locale
        for (i in 0 until locales.size) {
            locale = locales.valueAt(i)
            if (LANG_AUTO == locales.keyAt(i)) {
                localesL[i] = LANG_AUTO
            } else localesL[i] = locale.getDisplayName(locale)
        }
        return localesL
    }
}