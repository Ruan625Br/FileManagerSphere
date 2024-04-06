/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - SphereApplication.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data

import android.app.Application
import android.content.Intent
import com.etb.filemanager.manager.files.services.ScreenshotObserverService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SphereApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, ScreenshotObserverService::class.java))
    }

}