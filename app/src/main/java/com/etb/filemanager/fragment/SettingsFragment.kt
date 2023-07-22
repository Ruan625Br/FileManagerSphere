package com.etb.filemanager.fragment

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.os.LocaleList
import android.util.ArrayMap
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import com.etb.filemanager.R
import com.etb.filemanager.activity.MainActivity
import com.etb.filemanager.files.util.LangUtils
import com.etb.filemanager.settings.preference.PreferenceFragment
import com.etb.filemanager.settings.preference.Preferences
import com.etb.filemanager.settings.preference.SettingsDataStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale
import java.util.Objects


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

        //Language

        var mCurrentLang = Preferences.Interface.language
        val locales: ArrayMap<String, Locale> = LangUtils().getAppLanguages(requireActivity())
        val languageNames = getLanguagesL(locales)
        val languages = arrayOfNulls<String>(languageNames.size)
        for (i in 0 until locales.size) {
            languages[i] = locales.keyAt(i)
        }
        var localeIndex = locales.indexOfKey(mCurrentLang)
        if (localeIndex < 0) {
            localeIndex = locales.indexOfKey(LangUtils().LANG_AUTO)
        }
        val locale: Preference = Objects.requireNonNull(findPreference("custom_locale"))
        locale.summary = languageNames[localeIndex]
        locale.setOnPreferenceClickListener { preference ->
            MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.pref_app_language_title))
                .setSingleChoiceItems(languageNames, localeIndex) { dialog, which ->
                    if (which != localeIndex) {
                        mCurrentLang = languages[which]!!
                        Preferences.Interface.language = languages[which]!!
                        updateAppLanguage(locales[languages[which]]!!)
                        restartActivity()
                    }

                    dialog.dismiss()
                }

                .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, which ->
                }.show()
            true
        }
        locale.setOnPreferenceChangeListener { preference, newValue ->
            locale.summary = languageNames[localeIndex]

            true
        }


    }

     private fun updateAppLanguage(locale: Locale) {
        Locale.setDefault(locale)

        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)

        val dm = resources.displayMetrics
        resources.updateConfiguration(configuration, dm)
    }

    private fun restartActivity() {
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }



    private fun getLanguagesL(locales: ArrayMap<String, Locale>): Array<CharSequence?> {
        val localesL = arrayOfNulls<CharSequence>(locales.size)
        var locale: Locale?
        for (i in 0 until locales.size) {
            locale = locales.valueAt(i)
            if (locale != null) {
                if (LangUtils().LANG_AUTO == locales.keyAt(i)) {
                    localesL[i] = requireActivity().getString(R.string.auto)
                } else {
                    localesL[i] = locale.getDisplayName(locale)
                }
            } else {
                // Defina aqui um valor padrão caso a localização seja nula.
                // Por exemplo, você pode usar "Automático" para a chave LANG_AUTO.
                localesL[i] = LangUtils().LANG_AUTO
            }
        }
        return localesL
    }
}


