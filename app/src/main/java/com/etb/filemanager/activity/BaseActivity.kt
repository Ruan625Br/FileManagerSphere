package com.etb.filemanager.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.etb.filemanager.files.util.Args
import com.etb.filemanager.files.util.LocaleContextWrapper
import com.etb.filemanager.files.util.putArgs
import com.etb.filemanager.settings.preference.Preferences
import com.etb.filemanager.util.file.style.StyleManager

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme()
        super.onCreate(savedInstanceState)

    }

    private fun applyTheme() {
        val styleManager = StyleManager()
        val optionStyle = StyleManager.OptionStyle.valueOf(Preferences.Appearance.appTheme)
        styleManager.setTheme(optionStyle, this)
    }

    fun applyConfigurationChangesToActivities(savedInstanceState: Bundle) {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putArgs(Args(savedInstanceState))
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finishAffinity()
    }
    override fun attachBaseContext(newBase: Context) {
        val context = LocaleContextWrapper.wrap(newBase)
        super.attachBaseContext(context)
    }
}
