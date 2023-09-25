/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FileAdapterListenerUtil.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.interfaces.manager

import android.annotation.SuppressLint
import android.content.Context
import com.etb.filemanager.interfaces.settings.util.SelectPreferenceUtils
import com.etb.filemanager.manager.adapter.FileModel

class FileAdapterListenerUtil {
     private lateinit var  listener: FileAdapterListener
     private lateinit var context: Context

    fun setListener(mListener: FileAdapterListener, mContext: Context){
        listener = mListener
        context = mContext
    }

    fun addItemOnLongClick(item: FileModel, isActionMode: Boolean){
         listener.onLongClickListener(item, isActionMode)
    }

    fun addItemClick(item: FileModel, path: String, isDirectory: Boolean){
        listener.onItemClick(item, path, isDirectory)
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: FileAdapterListenerUtil? = null

        fun getInstance(): FileAdapterListenerUtil {
            if (instance == null) {
                instance = FileAdapterListenerUtil()
            }
            return instance!!
        }
    }
}