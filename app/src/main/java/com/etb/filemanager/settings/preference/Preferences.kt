package com.etb.filemanager.settings.preference

import android.content.Context
import com.etb.filemanager.manager.files.filelist.FileSortOptions
import com.etb.filemanager.manager.files.filelist.FileSortOptions.SortBy
import com.etb.filemanager.util.file.jsonStringToList
import com.etb.filemanager.util.file.stringListToJsonString
import com.etb.filemanager.util.file.style.StyleManager


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

        fun getAppTheme(): Int {
            return StyleManager().getAppTheme(StyleManager.OptionStyle.valueOf(appTheme))
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
        var viewFileInformationOption: InterfacePreferences.ViewFileInformationOption
            get() = InterfacePreferences.ViewFileInformationOption.valueOf(
                AppPreference.getString(
                    AppPreference.PreferenceKey.PREF_VIEW_FILE_INFORMATION_STR
                )
            )
            set(value) {
                AppPreference.set(
                    AppPreference.PreferenceKey.PREF_VIEW_FILE_INFORMATION_STR, value.name
                )
            }
        var language: String
            get() = AppPreference.getString(AppPreference.PreferenceKey.PREF_CUSTOM_LOCALE_STR)
            set(value) {
                AppPreference.set(AppPreference.PreferenceKey.PREF_CUSTOM_LOCALE_STR, value)
            }

        fun getLanguage(context: Context): String {
            val appPreference = AppPreference.getNewInstace(context)
            return (appPreference.getValue(AppPreference.PreferenceKey.PREF_CUSTOM_LOCALE_STR) as String)
        }

        var isEnabledTransparentListBackground: Boolean
            get() = AppPreference.getBoolean(AppPreference.PreferenceKey.PREF_TRANSPARENT_LIST_BACKGROUND_BOOL)
            set(value) {
                AppPreference.set(
                    AppPreference.PreferenceKey.PREF_TRANSPARENT_LIST_BACKGROUND_BOOL, value
                )
            }

        var selectedFileBackgroundOpacity: Float
            get() = AppPreference.getString(AppPreference.PreferenceKey.PREF_SELECTED_FILE_BACKGROUND_OPACITY_STR)
                .toFloat()
            set(value) {
                AppPreference.set(
                    AppPreference.PreferenceKey.PREF_SELECTED_FILE_BACKGROUND_OPACITY_STR,
                    value.toString()
                )
            }
        var fileListMargins: Int
            get() = AppPreference.getString(AppPreference.PreferenceKey.PREF_FILE_LIST_MARGINS_STR)
                .toInt()
            set(value) {
                AppPreference.set(
                    AppPreference.PreferenceKey.PREF_FILE_LIST_MARGINS_STR, value.toString()
                )
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
