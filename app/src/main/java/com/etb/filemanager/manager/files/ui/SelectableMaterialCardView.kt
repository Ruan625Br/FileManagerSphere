package com.etb.filemanager.manager.files.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.etb.filemanager.settings.preference.Preferences
import com.google.android.material.card.MaterialCardView


class SelectableMaterialCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {
    private var _isChecked = false
        set(value) {
            if (field == value){
                return
            }
            field = value
            refreshDrawableState()
        }


    override fun onCreateDrawableState(extraSpace: Int): IntArray =
         super.onCreateDrawableState(extraSpace).apply{
             if (_isChecked){
                 View.mergeDrawableStates(this, CHECKED_STATE_SET)
             }
         }


    override fun setRadius(radius: Float) {
        if (Preferences.Interface.isEnabledRoundedCorners){
        val mRadius = 20f
        super.setRadius(mRadius)
        }
    }
    override fun toggle() {
        _isChecked = !_isChecked
    }

    override fun setChecked(checked: Boolean) {
        _isChecked = checked
    }
    override fun isChecked(): Boolean  = _isChecked

    companion object{
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)

    }

}