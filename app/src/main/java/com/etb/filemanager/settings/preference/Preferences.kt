package com.etb.filemanager.settings.preference;

public class Preferences {

    public static final class Appearance {

        public static String getAppTheme() {
            return AppPreference.getString(AppPreference.PreferenceKey.PREF_APP_THEME_STR);
        }

        public static void setAppTheme(String theme){
            AppPreference.set(AppPreference.PreferenceKey.PREF_APP_THEME_STR, theme);
        }

        public static Boolean isEnabledAnimFileList() {
            return AppPreference.getBoolean(AppPreference.PreferenceKey.PREF_APP_ANIM_FILES_LIST_BOOL);
        }

        public static void setAnimFileList(Boolean isEnabled){
            AppPreference.set(AppPreference.PreferenceKey.PREF_APP_ANIM_FILES_LIST_BOOL, isEnabled);
        }
    }

    public static final class Behavior {
        public static String getDefaultFolder() {
            return AppPreference.getString(AppPreference.PreferenceKey.PREF_DEFAULT_FOLDER_STR);
        }

        public static void setDefaultFolder(String defaultFolder) {
            AppPreference.set(AppPreference.PreferenceKey.PREF_DEFAULT_FOLDER_STR, defaultFolder);
        }
    }


}
