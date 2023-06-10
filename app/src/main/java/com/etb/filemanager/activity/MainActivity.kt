package com.etb.filemanager.activity


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.etb.filemanager.R
import com.etb.filemanager.fragment.HomeFragment
import com.etb.filemanager.fragment.StartedFragment
import com.etb.filemanager.interfaces.settings.PopupSettingsListener
import com.etb.filemanager.settings.PreferenceUtils
import com.etb.filemanager.settings.preference.PopupSettings
import com.etb.filemanager.util.file.style.StyleManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar


class MainActivity : AppCompatActivity() {
    private lateinit var popupSettings: PopupSettings
    private lateinit var preferenceUtils: PreferenceUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startTheme()
        setContentView(R.layout.activity_main)


        popupSettings = PopupSettings(this)
        preferenceUtils = PreferenceUtils(this)
        if (preferenceUtils.isNewUser()){
            val startedFragment = StartedFragment()
            starNewFragment(startedFragment)
        }

    }



    fun starNewFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
     //   transaction.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left, R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
        transaction.replace(R.id.fragment_container_view, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun startTheme(){
        val styleManager = StyleManager()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val styleString = sharedPreferences.getString("themes", StyleManager.OptionStyle.FOLLOW_SYSTEM.name)
        val optionStyle = StyleManager.OptionStyle.valueOf(styleString ?: StyleManager.OptionStyle.FOLLOW_SYSTEM.name)
        styleManager.setTheme(optionStyle, this)

    }


}