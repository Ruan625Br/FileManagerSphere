/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - SelectableMaterialCardView.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.files.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.etb.filemanager.R
import com.etb.filemanager.files.util.getDimension
import com.etb.filemanager.files.util.getDimensionPixelSize
import com.etb.filemanager.files.util.getInteger
import com.etb.filemanager.settings.preference.Preferences
import com.google.android.material.card.MaterialCardView


class SelectableMaterialCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {
    private var _isChecked = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            refreshDrawableState()
        }


    override fun onCreateDrawableState(extraSpace: Int): IntArray =
        super.onCreateDrawableState(extraSpace).apply {
            if (_isChecked) {
                View.mergeDrawableStates(this, CHECKED_STATE_SET)
            }
        }


    override fun toggle() {
        _isChecked = !_isChecked
    }

    override fun setChecked(checked: Boolean) {
        _isChecked = checked
    }

    override fun isChecked(): Boolean = _isChecked

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)

    }

}