/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - StyleManager.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.ui.style

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.etb.filemanager.R

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
            OptionStyle.DYNAMIC_COLORS -> enableDynamicColors(context)
            OptionStyle.MATERIAL_DESIGN_TWO -> enableMaterialDesignTwo(context)
        }
    }

    private fun setFollowSystemTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    private fun setLightTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }
    /**
     * If you are switching from a light theme to a dark theme, it's recommended to call
     * {@link #setDarkTheme} before applying any other dark theme. This can help avoid
     * rendering issues on some devices.
     */

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
        setDarkTheme()
        context.setTheme(R.style.AppThemeGreen_Dark)

    }

    private fun setLightBlueTheme(context: Context) {
        context.setTheme(R.style.AppThemeBlue_light)
    }

    private fun setDarkBlueTheme(context: Context) {
        setDarkTheme()
        context.setTheme(R.style.AppThemeBlue_Dark)
    }

    private fun setLightRedTheme(context: Context) {
        context.setTheme(R.style.AppThemeRed_light)
    }

    private fun setDarkRedTheme(context: Context) {
        setDarkTheme()
        context.setTheme(R.style.AppThemeRed_Dark)
    }

    private fun enableDynamicColors(context: Context) {
        setDarkTheme()
        context.setTheme(R.style.Theme_MaterialYouColors)
    }
    private fun enableMaterialDesignTwo(context: Context) {
        setDarkTheme()
        context.setTheme(R.style.AppThemeViolet_Material2_Dark)
    }

    fun getAppTheme(optionStyle: OptionStyle): Int {
        return when (optionStyle) {
            OptionStyle.FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            OptionStyle.LIGHT_THEME -> AppCompatDelegate.MODE_NIGHT_NO
            OptionStyle.DARK_THEME -> AppCompatDelegate.MODE_NIGHT_YES
            OptionStyle.PINK_THEME -> R.style.AppThemePink
            OptionStyle.GREEN_THEME_LIGHT -> R.style.AppThemeGreen_Ligth
            OptionStyle.GREEN_THEME_DARK -> R.style.AppThemeGreen_Dark
            OptionStyle.BLUE_THEME_LIGHT -> R.style.AppThemeBlue_light
            OptionStyle.BLUE_THEME_DARK -> R.style.AppThemeBlue_Dark
            OptionStyle.RED_THEME_LIGHT -> R.style.AppThemeRed_light
            OptionStyle.RED_THEME_DARK -> R.style.AppThemeRed_Dark
            OptionStyle.DYNAMIC_COLORS -> R.style.Theme_MaterialYouColors
            OptionStyle.MATERIAL_DESIGN_TWO -> R.style.AppThemeViolet_Material2_Dark
        }
    }
    fun resetToDefaultTheme(){
        setFollowSystemTheme()
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
        RED_THEME_DARK,
        DYNAMIC_COLORS,
        MATERIAL_DESIGN_TWO
    }
}
