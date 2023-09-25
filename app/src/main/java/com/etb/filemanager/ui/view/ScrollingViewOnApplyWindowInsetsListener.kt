/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ScrollingViewOnApplyWindowInsetsListener.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */
package com.etb.filemanager.ui.view

import android.graphics.Rect
import android.view.View
import android.view.WindowInsets
import me.zhanghai.android.fastscroll.FastScroller

class ScrollingViewOnApplyWindowInsetsListener(
    view: View,
    private val fastScroller: FastScroller
) : View.OnApplyWindowInsetsListener {
    private val initialPadding =
        Rect(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

    init {
        fastScroller.setPadding(0, 0, 0, 0)
    }

    override fun onApplyWindowInsets(view: View, insets: WindowInsets): WindowInsets {
        view.setPadding(
            initialPadding.left, initialPadding.top, initialPadding.right,
            initialPadding.bottom + insets.systemWindowInsetBottom
        )
        fastScroller.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
        return insets
    }
}