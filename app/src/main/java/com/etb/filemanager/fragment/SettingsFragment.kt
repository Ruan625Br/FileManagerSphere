package com.etb.filemanager.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.preference.PreferenceFragmentCompat
import com.etb.filemanager.R
import com.etb.filemanager.activity.MainActivity
import com.etb.filemanager.files.util.BundleParceler
import com.etb.filemanager.util.file.style.StyleManager
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val styleManager = StyleManager()



    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

       when (key){
           "themes"-> {
               val styleString = preferenceManager.sharedPreferences?.getString(key, StyleManager.OptionStyle.FOLLOW_SYSTEM.name)
               val optionStyle = StyleManager.OptionStyle.valueOf(styleString ?: StyleManager.OptionStyle.FOLLOW_SYSTEM.name)

               styleManager.setTheme(optionStyle, requireContext())
               restartActivity()

           }
       }
    }


    private fun restartActivity() {
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }
}


