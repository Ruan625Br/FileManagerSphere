package com.etb.filemanager.ui.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
import com.google.android.material.appbar.MaterialToolbar

class MaterialToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialToolbar(context, attrs, defStyleAttr) {

    init {
        val scrollFlags = (SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP)
        layoutParams = LayoutParams(scrollFlags).apply {

        }
    }
}