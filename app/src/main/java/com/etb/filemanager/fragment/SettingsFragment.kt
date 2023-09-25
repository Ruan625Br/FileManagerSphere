/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - SettingsFragment.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.collection.ArrayMap
import androidx.preference.Preference
import com.etb.filemanager.R
import com.etb.filemanager.activity.BaseActivity
import com.etb.filemanager.activity.SettingsActivity
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

    @SuppressLint("ResourceType")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        preferenceManager.preferenceDataStore = SettingsDataStore()

        //Language

        var mCurrentLang = Preferences.Interface.language
        val locales = LangUtils.getAppLanguages(requireActivity())
        val languageNames = getLanguagesL(locales)
        val languages = arrayOfNulls<String>(languageNames.size)
        for (i in 0 until locales.size) {
            languages[i] = locales.keyAt(i)
        }
        var localeIndex = locales.indexOfKey(mCurrentLang)
        if (localeIndex < 0) {
            localeIndex = locales.indexOfKey(LangUtils.LANG_AUTO)
        }
        val locale: Preference = Objects.requireNonNull(findPreference("custom_locale"))
        locale.summary = languageNames[localeIndex]
        locale.setOnPreferenceClickListener { preference ->
            MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.pref_app_language_title))
                .setSingleChoiceItems(languageNames, localeIndex) { dialog, which ->
                    if (which != localeIndex) {
                        mCurrentLang = languages[which]!!
                        Preferences.Interface.language = languages[which]!!
                        (activity as SettingsActivity).restart()

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

    private fun getLanguagesL(locales: ArrayMap<String, Locale>): Array<CharSequence?> {
        val localesL = arrayOfNulls<CharSequence>(locales.size)

        for (i in 0 until locales.size) {
            val locale = locales.valueAt(i)
            if (LangUtils.LANG_AUTO == locales.keyAt(i)) {
                localesL[i] = requireContext().getString(R.string.auto)
            } else {
                localesL[i] = locale?.getDisplayName(locale)
            }
        }

        return localesL
    }

}


