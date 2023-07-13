package com.etb.filemanager.manager.files.ui


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.etb.filemanager.R
import com.etb.filemanager.files.util.asColor
import com.etb.filemanager.files.util.getColorByAttr
import com.etb.filemanager.files.util.shortAnimTime
import com.etb.filemanager.files.util.withModulatedAlpha
import com.etb.filemanager.settings.preference.Preferences
import com.etb.filemanager.util.file.style.IconUtil


object CheckableItemBackground {

    @SuppressLint("RestrictedApi")
    fun create(context: Context): Drawable{
        val typedValue = TypedValue()
        val resolved = context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValue, true)
        val colorOnSecondary = if (resolved) typedValue.data else 0


        return AnimatedStateListDrawableCompat().apply {
            val shortAnimTime = context.shortAnimTime
            setEnterFadeDuration(shortAnimTime)
            setExitFadeDuration(shortAnimTime)
            val primaryColor = context.getColorByAttr( com.google.android.material.R.attr.colorPrimaryContainer)
            val checkedColor = primaryColor.asColor().withModulatedAlpha(0.48f).value
            val normalColor = colorOnSecondary

            val backgroundSelected = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.background_file_item_selected)!!)
            DrawableCompat.setTint(backgroundSelected, checkedColor)

            val background = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.background_file_item)!!)
            DrawableCompat.setTint(background, normalColor)
            if (Preferences.Interface.isEnabledRoundedCorners) {
                addState(intArrayOf(android.R.attr.state_checked), backgroundSelected)
                addState(intArrayOf(), background)
            } else{
                addState(intArrayOf(android.R.attr.state_checked), ColorDrawable(checkedColor))
                addState(intArrayOf(), ColorDrawable(normalColor))
            }
        }
}}