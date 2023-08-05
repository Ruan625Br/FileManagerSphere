package com.etb.filemanager.settings.preference

// SPDX-License-Identifier: GPL-3.0-or-later


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.etb.filemanager.ui.view.UiUtils

abstract class PreferenceFragment : PreferenceFragmentCompat() {
    companion object {
        const val PREF_KEY = "key"
    }

    private var mPrefKey: String? = null

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            mPrefKey = it.getString(PREF_KEY)
            it.remove(PREF_KEY)
        }
        // https://github.com/androidx/androidx/blob/androidx-main/preference/preference/res/layout/preference_recyclerview.xml
        val recyclerView = view.findViewById<RecyclerView>(androidx.preference.R.id.recycler_view)
        recyclerView.fitsSystemWindows = true
        recyclerView.clipToPadding = false
        UiUtils.applyWindowInsetsAsPaddingNoTop(recyclerView)
    }


    @CallSuper
    override fun onStart() {
        requireActivity().setTitle(getTitle())
        super.onStart()
        updateUi()
    }

    @StringRes
    abstract fun getTitle(): Int

    fun setPrefKey(prefKey: String?) {
        mPrefKey = prefKey
        updateUi()
    }


    @SuppressLint("RestrictedApi")
    private fun updateUi() {
        mPrefKey?.let {
            val prefToNavigate = findPreference<Preference>(it)
            prefToNavigate?.performClick()
        }
    }
}

