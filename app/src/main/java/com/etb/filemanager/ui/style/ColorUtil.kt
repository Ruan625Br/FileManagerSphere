package com.etb.filemanager.ui.style

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.R


class ColorUtil {


    fun getColorPrimaryInverse(context: Context): Int {

        val attrs = intArrayOf(R.attr.colorPrimaryInverse)
        val typedArray = context.obtainStyledAttributes(attrs)
        val tint = typedArray.getColor(0, 0)
        typedArray.recycle()

        return tint
    }

    fun setTintDrawable(tint: Int, drawable: Drawable): Drawable {

        val tintedDrawable = DrawableCompat.wrap(drawable).mutate()
        DrawableCompat.setTint(tintedDrawable, tint)

        return drawable
    }

}