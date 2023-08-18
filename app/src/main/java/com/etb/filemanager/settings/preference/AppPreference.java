package com.etb.filemanager.settings.preference;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import com.etb.filemanager.R;
import com.etb.filemanager.files.util.ArrayUtils;
import com.etb.filemanager.files.util.ContextUtils;
import com.etb.filemanager.files.util.LangUtils;
import com.etb.filemanager.manager.files.filelist.FileSortOptions;
import com.etb.filemanager.ui.style.StyleManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
public class AppPreference {
    private static final String PREFERENCE_NAME = "preferences";
    private static final int PREFERENCE_SKIP = 5;

    @Keep
    public enum PreferenceKey {
        //
        PREF_APP_THEME_STR,
        PREF_MATERIAL_DESIGN_3_BOOL,


        //Interface
        PREF_ANIM_FILES_LIST_BOOL,
        PREF_ROUNDED_CORNERS_BOOL,
        PREF_VIEW_FILE_INFORMATION_STR,
        PREF_CUSTOM_LOCALE_STR,
        PREF_TRANSPARENT_LIST_BACKGROUND_BOOL,
        PREF_SELECTED_FILE_BACKGROUND_OPACITY_STR,
        PREF_FILE_LIST_MARGINS_STR,

        //Behavior
        PREF_DEFAULT_FOLDER_STR,
        PREF_SELECT_FILE_LONG_CLICK_BOOL,
        PREF_LIST_CATEGORIES_NAME_STR,
        PREF_LIST_CATEGORIES_PATH_STR,
        PREF_SHOW_FAST_SCROLL_BOOL,

        //Popup
        PREF_SORT_BY_STR,
        PREF_ORDER_FILES_STR,
        PREF_DIRECTORIES_FIRST_BOOL,
        PREF_SHOW_HIDDEN_FILE_BOOL,
        PREF_GRID_TOGGLE_BOOL
        ;

        private static final String[] sKeys = new String[values().length];
        @Type
        private static final int[] sTypes = new int[values().length];
        private static final List<PreferenceKey> sPrefKeyList = Arrays.asList(values());

        static {
            String keyStr;
            int typeSeparator;
            PreferenceKey[] keyValues = values();
            for (int i = 0; i < keyValues.length; ++i) {
                keyStr = keyValues[i].name();
                typeSeparator = keyStr.lastIndexOf('_');
                sKeys[i] = keyStr.substring(PREFERENCE_SKIP, typeSeparator).toLowerCase(Locale.ROOT);
                sTypes[i] = inferType(keyStr.substring(typeSeparator + 1));
            }
        }
        public static int indexOf(PreferenceKey key){
            return sPrefKeyList.indexOf(key);
        }
        public static int indexOf(String key){
            return ArrayUtils.indexOf(sKeys, key);
        }

        @Type
        private static int inferType(@NonNull String typeName) {
            switch (typeName) {
                case "BOOL":
                    return TYPE_BOOLEAN;
                case "FLOAT":
                    return TYPE_FLOAT;
                case "INT":
                    return TYPE_INTEGER;
                case "LONG":
                    return TYPE_LONG;
                case "STR":
                    return TYPE_STRING;
                default:
                    throw new IllegalArgumentException("Unsupported type.");
            }
        }
    }

    @IntDef(value = {
            TYPE_BOOLEAN,
            TYPE_FLOAT,
            TYPE_INTEGER,
            TYPE_LONG,
            TYPE_STRING
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final int TYPE_BOOLEAN = 0;
    public static final int TYPE_FLOAT = 1;
    public static final int TYPE_INTEGER = 2;
    public static final int TYPE_LONG = 3;
    public static final int TYPE_STRING = 4;

    private static AppPreference sAppPref;

    @NonNull
    public static AppPreference getInstance(){
        if (sAppPref == null){
            sAppPref = new AppPreference(ContextUtils.getContext());
        }
        return sAppPref;
    }

    @NonNull static AppPreference getNewInstace(@NonNull Context context){
        return new AppPreference(context);
    }

    @NonNull
    public static Object get(PreferenceKey key) {
        int index = PreferenceKey.indexOf(key);
        AppPreference appPref = getInstance();
        switch (PreferenceKey.sTypes[index]) {
            case TYPE_BOOLEAN:
                return appPref.mPreferences.getBoolean(PreferenceKey.sKeys[index], (boolean) appPref.getDefaultValue(key));
            case TYPE_FLOAT:
                return appPref.mPreferences.getFloat(PreferenceKey.sKeys[index], (float) appPref.getDefaultValue(key));
            case TYPE_INTEGER:
                return appPref.mPreferences.getInt(PreferenceKey.sKeys[index], (int) appPref.getDefaultValue(key));
            case TYPE_LONG:
                return appPref.mPreferences.getLong(PreferenceKey.sKeys[index], (long) appPref.getDefaultValue(key));
            case TYPE_STRING:
                return Objects.requireNonNull(appPref.mPreferences.getString(PreferenceKey.sKeys[index],
                        (String) appPref.getDefaultValue(key)));
        }
        throw new IllegalArgumentException("Unknown key or type.");
    }

    public static boolean getBoolean(PreferenceKey key) {
        return (boolean) get(key);
    }

    public static int getInt(PreferenceKey key) {
        return (int) get(key);
    }

    public static long getLong(PreferenceKey key) {
        return (long) get(key);
    }

    @NonNull
    public static String getString(PreferenceKey key) {
        return (String) get(key);
    }

    public static void set(PreferenceKey key, Object value) {
        getInstance().setPref(key, value);
    }

    @NonNull
    private final SharedPreferences mPreferences;
    @NonNull
    private final SharedPreferences.Editor mEditor;

    private final Context mContext;


    @SuppressLint("CommitPrefEdits")
    private AppPreference(@NonNull Context context) {
        mContext = context;
        mPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        init();
    }

    public void setPref(PreferenceKey key, Object value) {
        int index = PreferenceKey.indexOf(key);
        if (value instanceof Boolean) mEditor.putBoolean(PreferenceKey.sKeys[index], (Boolean) value);
        else if (value instanceof Float) mEditor.putFloat(PreferenceKey.sKeys[index], (Float) value);
        else if (value instanceof Integer) mEditor.putInt(PreferenceKey.sKeys[index], (Integer) value);
        else if (value instanceof Long) mEditor.putLong(PreferenceKey.sKeys[index], (Long) value);
        else if (value instanceof String) mEditor.putString(PreferenceKey.sKeys[index], (String) value);
        mEditor.apply();
        mEditor.commit();
    }

    public void setPref(String key, @Nullable Object value) {
        int index = PreferenceKey.indexOf(key);
        if (index == -1) throw new IllegalArgumentException("Invalid key: " + key);
        if (value == null) value = getDefaultValue(PreferenceKey.sPrefKeyList.get(index));
        if (value instanceof Boolean) mEditor.putBoolean(key, (Boolean) value);
        else if (value instanceof Float) mEditor.putFloat(key, (Float) value);
        else if (value instanceof Integer) mEditor.putInt(key, (Integer) value);
        else if (value instanceof Long) mEditor.putLong(key, (Long) value);
        else if (value instanceof String) mEditor.putString(key, (String) value);
        mEditor.apply();
        mEditor.commit();
    }

    @NonNull
    public Object get(String key) {
        int index = PreferenceKey.indexOf(key);
        if (index == -1) throw new IllegalArgumentException("Invalid key: " + key);
        Object defaultValue = getDefaultValue(PreferenceKey.sPrefKeyList.get(index));
        switch (PreferenceKey.sTypes[index]) {
            case TYPE_BOOLEAN:
                return mPreferences.getBoolean(key, (boolean) defaultValue);
            case TYPE_FLOAT:
                return mPreferences.getFloat(key, (float) defaultValue);
            case TYPE_INTEGER:
                return mPreferences.getInt(key, (int) defaultValue);
            case TYPE_LONG:
                return mPreferences.getLong(key, (long) defaultValue);
            case TYPE_STRING:
                return Objects.requireNonNull(mPreferences.getString(key, (String) defaultValue));
        }
        throw new IllegalArgumentException("Unknown key or type.");
    }

    @NonNull
    public Object getValue(PreferenceKey key) {
        int index = PreferenceKey.indexOf(key);
        switch (PreferenceKey.sTypes[index]) {
            case TYPE_BOOLEAN:
                return mPreferences.getBoolean(PreferenceKey.sKeys[index], (boolean) getDefaultValue(key));
            case TYPE_FLOAT:
                return mPreferences.getFloat(PreferenceKey.sKeys[index], (float) getDefaultValue(key));
            case TYPE_INTEGER:
                return mPreferences.getInt(PreferenceKey.sKeys[index], (int) getDefaultValue(key));
            case TYPE_LONG:
                return mPreferences.getLong(PreferenceKey.sKeys[index], (long) getDefaultValue(key));
            case TYPE_STRING:
                return Objects.requireNonNull(mPreferences.getString(PreferenceKey.sKeys[index],
                        (String) getDefaultValue(key)));
        }
        throw new IllegalArgumentException("Unknown key or type.");
    }

    private void init() {
        for (int i = 0; i < PreferenceKey.sKeys.length; ++i) {
            if (!mPreferences.contains(PreferenceKey.sKeys[i])) {
                switch (PreferenceKey.sTypes[i]) {
                    case TYPE_BOOLEAN:
                        mEditor.putBoolean(PreferenceKey.sKeys[i], (boolean) getDefaultValue(PreferenceKey.sPrefKeyList.get(i)));
                        break;
                    case TYPE_FLOAT:
                        mEditor.putFloat(PreferenceKey.sKeys[i], (float) getDefaultValue(PreferenceKey.sPrefKeyList.get(i)));
                        break;
                    case TYPE_INTEGER:
                        mEditor.putInt(PreferenceKey.sKeys[i], (int) getDefaultValue(PreferenceKey.sPrefKeyList.get(i)));
                        break;
                    case TYPE_LONG:
                        mEditor.putLong(PreferenceKey.sKeys[i], (long) getDefaultValue(PreferenceKey.sPrefKeyList.get(i)));
                        break;
                    case TYPE_STRING:
                        mEditor.putString(PreferenceKey.sKeys[i], (String) getDefaultValue(PreferenceKey.sPrefKeyList.get(i)));
                }
            }
        }
        mEditor.apply();
    }

    @NonNull
    public Object getDefaultValue(@NonNull PreferenceKey key){
        switch (key){
            case PREF_APP_THEME_STR:
                return "FOLLOW_SYSTEM";
              case PREF_DEFAULT_FOLDER_STR:
                return mContext.getResources().getString(R.string.default_pref_default_folder);
            case PREF_SORT_BY_STR:
                return FileSortOptions.SortBy.NAME.name();
            case PREF_DIRECTORIES_FIRST_BOOL:
                return mContext.getResources().getBoolean(R.bool.default_is_directories_first);
            case PREF_ORDER_FILES_STR:
                return FileSortOptions.Order.ASCENDING.name();
            case PREF_SHOW_HIDDEN_FILE_BOOL:
                return mContext.getResources().getBoolean(R.bool.default_show_hidden_file);
            case PREF_GRID_TOGGLE_BOOL:
                return mContext.getResources().getBoolean(R.bool.default_grid_toggle);
            case PREF_SELECT_FILE_LONG_CLICK_BOOL:
                return mContext.getResources().getBoolean(R.bool.default_select_file_long_click);
            case PREF_ANIM_FILES_LIST_BOOL:
                return mContext.getResources().getBoolean(R.bool.default_is_enabled_anim_in_file_list);
            case PREF_ROUNDED_CORNERS_BOOL:
                return mContext.getResources().getBoolean(R.bool.default_is_enabled_rounded_corners);
            case PREF_MATERIAL_DESIGN_3_BOOL:
                return mContext.getResources().getBoolean(R.bool.default_is_enabled_material_design_3);
            case PREF_LIST_CATEGORIES_NAME_STR:
                return mContext.getResources().getString(R.string.default_list_categories_name);
            case PREF_LIST_CATEGORIES_PATH_STR:
                return mContext.getResources().getString(R.string.default_list_categories_path);
            case PREF_VIEW_FILE_INFORMATION_STR:
                return mContext.getResources().getString(R.string.default_view_file_information);
            case PREF_CUSTOM_LOCALE_STR:
                return "auto";
            case PREF_TRANSPARENT_LIST_BACKGROUND_BOOL:
                return mContext.getResources().getBoolean(R.bool.default_is_enabled_transparent_list_background);
            case PREF_SELECTED_FILE_BACKGROUND_OPACITY_STR:
                return mContext.getResources().getString(R.string.default_selected_file_background_opacity);
            case PREF_FILE_LIST_MARGINS_STR:
                return mContext.getString(R.string.default_file_list_margins);
            case PREF_SHOW_FAST_SCROLL_BOOL:
                return false;
        }
        throw new IllegalArgumentException("Pref key not found.");
    }

}
