package com.etb.filemanager.settings.preference

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.etb.filemanager.interfaces.settings.util.SelectPreferenceUtils


class PopupSettings(private var context: Context) {

    private val itemSortByName = 0
    private val itemSortByType = 0
    private val itemSortBySize = 0
    private val itemSortByLastModified = 0
    private val selectPreferenceUtils: SelectPreferenceUtils = SelectPreferenceUtils.getInstance()

    private val sharedPopupSettingsActionSort: SharedPreferences =
        context.getSharedPreferences("sharedPopupSettingsActionSort", Context.MODE_PRIVATE)
    private var settingsModified: Boolean
        get() = sharedPopupSettingsActionSort.getBoolean("settings_action_sort_modified", false)
        set(value) = sharedPopupSettingsActionSort.edit()
            .putBoolean("settings_action_sort_modified", value).apply()


    fun itemIsSelectedActionSort(position: Int): Boolean {
        val itemSelectedActionSort =
            sharedPopupSettingsActionSort.getInt("settings_action_sort_item_selected", 0)
        return if (settingsModified) {
            position == itemSelectedActionSort

        } else {
            if (!settingsModified) {
                settingsModified = true
            }
            if (position == 0) {
                true
            } else {
                true
            }

        }
    }


    fun setItemSelectedActionSort(position: Int) {
        val itemSelectedActionSort =
            sharedPopupSettingsActionSort.getInt("settings_action_sort_item_selected", 0)
        if (position != itemSelectedActionSort) {
            sharedPopupSettingsActionSort.edit()
                .putInt("settings_action_sort_item_selected", position).apply()
            updateItemSelectedActionSortListener(position)
            Log.i("SETTINGS", "ITEM $itemSelectedActionSort")
        }
        if (!settingsModified) {
            settingsModified = true
        }


    }

    fun setSelectedActionSortFolderFirst() {
        if (getActionSortFolderFirst()) {
            sharedPopupSettingsActionSort.edit()
                .putBoolean("settings_action_sort_directories_first", false).apply()


        } else {
            sharedPopupSettingsActionSort.edit()
                .putBoolean("settings_action_sort_directories_first", true).apply()

        }


    }

    fun setSelectedActionShowHiddenFiles() {


        if (getActionShowHiddenFiles()) {
            sharedPopupSettingsActionSort.edit()
                .putBoolean("settings_action_show_hidden_files", false).apply()


        } else {
            sharedPopupSettingsActionSort.edit()
                .putBoolean("settings_action_show_hidden_files", true).apply()

        }

    }


    fun getActionShowHiddenFiles(): Boolean {
        return sharedPopupSettingsActionSort.getBoolean("settings_action_show_hidden_files", false)

    }

    fun getActionSortFolderFirst(): Boolean {
        return sharedPopupSettingsActionSort.getBoolean(
            "settings_action_sort_directories_first",
            false
        )
    }


    fun getItemSelectedActionSort(): Int {
        val sharedPopupSettingsActionSort: SharedPreferences =
            context.getSharedPreferences("sharedPopupSettingsActionSort", Context.MODE_PRIVATE)

        val itemSelectedActionSort =
            sharedPopupSettingsActionSort.getInt("settings_action_sort_item_selected", 0)
        return itemSelectedActionSort
    }

    fun updateItemSelectedActionSortListener(position: Int) {
        selectPreferenceUtils.addItemSelectedOnListener(position, getActionSortFolderFirst())

    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: PopupSettings? = null

        fun getInstance(context: Context): PopupSettings {
            if (instance == null) {
                instance = PopupSettings(context)
            }
            return instance!!
        }
    }
}
