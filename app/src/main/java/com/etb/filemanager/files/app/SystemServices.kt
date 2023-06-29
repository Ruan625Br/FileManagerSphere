package com.etb.filemanager.files.app


import android.content.ContentResolver
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager

val appClassLoader = AppProvider::class.java.classLoader

val contentResolver: ContentResolver by lazy { application.contentResolver }

val defaultSharedPreferences: SharedPreferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(application)
}
val packageManager: PackageManager by lazy { application.packageManager }

