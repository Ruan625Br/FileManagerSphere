/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - SelectPreferenceUtils.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.interfaces.settings.util

import android.content.Context
import android.content.SharedPreferences
import com.etb.filemanager.interfaces.settings.PopupSettingsListener
import com.etb.filemanager.manager.adapter.FileModel

class SelectPreferenceUtils {
    private lateinit var listener: PopupSettingsListener

    fun addItemSelectedOnListener(itemSelected: Int, itemSelectedFolderFirst: Boolean) {

        listener.onItemSelectedActionSort(itemSelected, itemSelectedFolderFirst)

    }


    fun setListener(mListener: PopupSettingsListener, context: Context) {
        listener = mListener

    }

    fun sendFileInfo(currentPath: String){
        listener.onFileInfoReceived(currentPath)
    }

    fun getItemSelectedActionSort(context: Context): Int {
        val sharedPopupSettingsActionSort: SharedPreferences =
            context.getSharedPreferences("sharedPopupSettingsActionSort", Context.MODE_PRIVATE)

        return  sharedPopupSettingsActionSort.getInt("settings_action_sort_item_selected", 0)
    }

    fun getActionSortFolderFirst(context: Context): Boolean {
        val sharedPopupSettingsActionSort: SharedPreferences =
            context.getSharedPreferences("sharedPopupSettingsActionSort", Context.MODE_PRIVATE)
        return sharedPopupSettingsActionSort.getBoolean("settings_action_sort_directories_first", false)
    }



    fun sortFilesBy(position: Int, fileModels: MutableList<FileModel>) {
        when (position) {
            0 -> sortFilesAlphabetically(fileModels)
            1 -> sortFilesByFileSize(fileModels)
            2 -> sortFilesByFileType(fileModels)
        }
    }
    fun sortFilesAuto(fileModels: MutableList<FileModel>, context: Context) {
        val position = getItemSelectedActionSort(context)

        if (getActionSortFolderFirst(context)){
            sortFilesByFolderFirst(position, fileModels)
        }else{
            when (position) {
                0 -> sortFilesAlphabetically(fileModels)
                1 -> sortFilesByFileSize(fileModels)
                2 -> sortFilesByFileType(fileModels)
            }
        }

    }

    private fun sortFilesByFileSize(fileModels: MutableList<FileModel>) {
        fileModels.sortWith(compareByDescending { it.fileSize })



    }

    private fun sortFilesByFileType(fileModels: MutableList<FileModel>) {
        fileModels.sortBy { it.fileExtension }


    }

    fun sortFilesByFolderFirst(position: Int, fileModels: MutableList<FileModel>) {
        fileModels.sortWith(compareBy({ it.isDirectory }, { getFileSortingCriteria(it, position) }))
    }

    private fun getFileSortingCriteria(fileModel: FileModel, position: Int): Comparable<*>? = when (position) {
        0 -> fileModel.fileName
        1 -> fileModel.fileExtension
        2 -> fileModel.fileSize
        else -> null
    }


    private fun sortFilesAlphabetically(fileModels: MutableList<FileModel>) {
        fileModels.sortBy { it.fileName }

    }

    companion object {
        private var instance: SelectPreferenceUtils? = null

        fun getInstance(): SelectPreferenceUtils {
            if (instance == null) {
                instance = SelectPreferenceUtils()
            }
            return instance!!
        }
    }

}