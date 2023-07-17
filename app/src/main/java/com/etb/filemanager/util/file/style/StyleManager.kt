package com.etb.filemanager.util.file.style

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.startActivity
import com.etb.filemanager.R
import com.etb.filemanager.activity.MainActivity
import com.google.android.material.color.DynamicColors

class StyleManager {

    fun setTheme(optionStyle: OptionStyle, context: Context) {
        when (optionStyle) {
            OptionStyle.FOLLOW_SYSTEM -> setFollowSystemTheme()
            OptionStyle.LIGHT_THEME -> setLightTheme()
            OptionStyle.DARK_THEME -> setDarkTheme()
            OptionStyle.PINK_THEME -> setPinkTheme(context)
            OptionStyle.GREEN_THEME_LIGHT -> setGreenLightTheme(context)
            OptionStyle.GREEN_THEME_DARK -> setGreenDarkTheme(context)
            OptionStyle.BLUE_THEME_LIGHT -> setLightBlueTheme(context)
            OptionStyle.BLUE_THEME_DARK -> setDarkBlueTheme(context)
            OptionStyle.RED_THEME_LIGHT -> setLightRedTheme(context)
            OptionStyle.RED_THEME_DARK -> setDarkRedTheme(context)
        }
    }

    private fun setFollowSystemTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    private fun setLightTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }

    private fun setDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun setPinkTheme(context: Context) {
        context.setTheme(R.style.AppThemePink)
    }

    private fun setGreenLightTheme(context: Context) {
       context.setTheme(R.style.AppThemeGreen_Ligth)

    }
    private fun setGreenDarkTheme(context: Context) {
       context.setTheme(R.style.AppThemeGreen_Dark)

    }

    private fun setLightBlueTheme(context: Context) {
        context.setTheme(R.style.AppThemeBlue_light)
    }
    private fun setDarkBlueTheme(context: Context) {
        context.setTheme(R.style.AppThemeBlue_Dark)
    }

    private fun setLightRedTheme(context: Context) {
        context.setTheme(R.style.AppThemeRed_light)
    }
    private fun setDarkRedTheme(context: Context) {
        context.setTheme(R.style.Theme_MaterialYouColors)
    }

    enum class OptionStyle {
        FOLLOW_SYSTEM,
        LIGHT_THEME,
        DARK_THEME,
        PINK_THEME,
        GREEN_THEME_LIGHT,
        GREEN_THEME_DARK,
        BLUE_THEME_LIGHT,
        BLUE_THEME_DARK,
        RED_THEME_LIGHT,
        RED_THEME_DARK
    }
}
