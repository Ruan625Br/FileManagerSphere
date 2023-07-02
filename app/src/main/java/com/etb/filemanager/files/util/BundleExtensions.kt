package com.etb.filemanager.files.util

import android.os.Bundle
import android.os.Parcelable
import com.etb.filemanager.files.app.appClassLoader



fun <T : Parcelable> Bundle.getParcelableSafe(key: String?): T? {
    classLoader = appClassLoader
    return getParcelable(key)
}