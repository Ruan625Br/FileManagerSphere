package com.etb.filemanager.settings.preference

import com.etb.filemanager.manager.files.filelist.FileSortOptions
import com.etb.filemanager.manager.files.filelist.FileSortOptions.SortBy
import com.etb.filemanager.util.file.jsonStringToList
import com.etb.filemanager.util.file.stringListToJsonString


class Preferences {
    object Appearance {
        var appTheme: String
            get() = AppPreference.getString(AppPreference.PreferenceKey.PREF_APP_THEME_STR)
            set(theme) {
                AppPreference.set(AppPreference.PreferenceKey.PREF_APP_THEME_STR, theme)
            }
        var isEnabledDynamicColors: Boolean
            get() = AppPreference.getBoolean(AppPreference.PreferenceKey.PREF_DYNAMIC_COLORS_BOOL)
            set(value) {
                AppPreference.set(AppPreference.PreferenceKey.PREF_DYNAMIC_COLORS_BOOL, value)
            }
    }

    object Interface {

        var isAnimationEnabledForFileList: Boolean
            get() = AppPreference.getBoolean(AppPreference.PreferenceKey.PREF_ANIM_FILES_LIST_BOOL)
            set(value) {
                AppPreference.set(AppPreference.PreferenceKey.PREF_ANIM_FILES_LIST_BOOL, value)
            }
        var isEnabledRoundedCorners: Boolean
            get() = AppPreference.getBoolean(AppPreference.PreferenceKey.PREF_ROUNDED_CORNERS_BOOL)
            set(value) {
                AppPreference.set(AppPreference.PreferenceKey.PREF_ROUNDED_CORNERS_BOOL, value)
            }
    }

    object Behavior {
        var defaultFolder: String
            get() = AppPreference.getString(AppPreference.PreferenceKey.PREF_DEFAULT_FOLDER_STR)
            set(defaultFolder) {
                AppPreference.set(
                    AppPreference.PreferenceKey.PREF_DEFAULT_FOLDER_STR, defaultFolder
                )
            }

        var selectFileLongClick: Boolean
            get() = AppPreference.getBoolean(AppPreference.PreferenceKey.PREF_SELECT_FILE_LONG_CLICK_BOOL)
            set(value) {
                AppPreference.set(
                    AppPreference.PreferenceKey.PREF_SELECT_FILE_LONG_CLICK_BOOL, value
                )
            }

        var categoryNameList: List<String>
            get() = jsonStringToList(AppPreference.getString(AppPreference.PreferenceKey.PREF_LIST_CATEGORIES_NAME_STR))
            set(value) {
                AppPreference.set(
                    AppPreference.PreferenceKey.PREF_LIST_CATEGORIES_NAME_STR,
                    stringListToJsonString(value)
                )
            }
        var categoryPathList: List<String>
            get() = jsonStringToList(AppPreference.getString(AppPreference.PreferenceKey.PREF_LIST_CATEGORIES_PATH_STR))
            set(value) {
                AppPreference.set(
                    AppPreference.PreferenceKey.PREF_LIST_CATEGORIES_PATH_STR,
                    stringListToJsonString(value)
                )
            }
    }

    object Popup {
        var sortBy: SortBy
            get() = SortBy.valueOf(AppPreference.getString(AppPreference.PreferenceKey.PREF_SORT_BY_STR))
            set(sortBy) {
                AppPreference.set(AppPreference.PreferenceKey.PREF_SORT_BY_STR, sortBy.name)
            }
        var isDirectoriesFirst: Boolean
            get() = AppPreference.getBoolean(AppPreference.PreferenceKey.PREF_DIRECTORIES_FIRST_BOOL)
            set(directoriesFirst) {
                AppPreference.set(
                    AppPreference.PreferenceKey.PREF_DIRECTORIES_FIRST_BOOL, directoriesFirst
                )
            }

        var orderFiles: FileSortOptions.Order
            get() = FileSortOptions.Order.valueOf(AppPreference.getString(AppPreference.PreferenceKey.PREF_ORDER_FILES_STR))
            set(value) {
                AppPreference.set(AppPreference.PreferenceKey.PREF_ORDER_FILES_STR, value.name)
            }
        var showHiddenFiles: Boolean
            get() = AppPreference.getBoolean(AppPreference.PreferenceKey.PREF_SHOW_HIDDEN_FILE_BOOL)
            set(value) {
                AppPreference.set(AppPreference.PreferenceKey.PREF_SHOW_HIDDEN_FILE_BOOL, value)
            }
        var isGridEnabled: Boolean
            get() = AppPreference.getBoolean(AppPreference.PreferenceKey.PREF_GRID_TOGGLE_BOOL)
            set(value) {
                AppPreference.set(AppPreference.PreferenceKey.PREF_GRID_TOGGLE_BOOL, value)
            }
    }
}
