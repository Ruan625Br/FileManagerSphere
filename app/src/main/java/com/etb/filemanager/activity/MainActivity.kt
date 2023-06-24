package com.etb.filemanager.activity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.etb.filemanager.R
import com.etb.filemanager.fragment.HomeFragment
import com.etb.filemanager.fragment.StartedFragment
import com.etb.filemanager.settings.PreferenceUtils
import com.etb.filemanager.settings.preference.PopupSettings
import com.etb.filemanager.util.file.style.StyleManager
import java.io.File


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
            preferenceUtils.newUser = false
            val startedFragment = StartedFragment()
            startNewFragment(startedFragment)
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOpenWithIntent(intent)
    }

    private fun handleOpenWithIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            val uri = intent.data
            if (uri != null) {
                val file = uri.path?.let { File(it) }
                Log.i("Chegou", "path ${file!!.path}")


                val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
                if (fragment is HomeFragment) {
                    fragment.onNewIntent(uri)
                } else {
                    val homeFragment = HomeFragment.newInstance(uri)
                    startNewFragment(homeFragment)
                }
            }
        }
    }


    fun startNewFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
     //   transaction.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left, R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
        transaction.replace(R.id.fragment_container_view, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun startTheme(){
        val styleManager = StyleManager()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val styleString = sharedPreferences.getString("themes", StyleManager.OptionStyle.FOLLOW_SYSTEM.name)
        val optionStyle = StyleManager.OptionStyle.valueOf(styleString ?: StyleManager.OptionStyle.FOLLOW_SYSTEM.name)
        styleManager.setTheme(optionStyle, this)

    }


}