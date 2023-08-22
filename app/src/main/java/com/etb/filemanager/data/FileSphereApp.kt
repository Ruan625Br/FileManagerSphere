package com.etb.filemanager.data

import android.app.Application
import com.etb.filemanager.data.database.AppDatabase

class FileSphereApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}