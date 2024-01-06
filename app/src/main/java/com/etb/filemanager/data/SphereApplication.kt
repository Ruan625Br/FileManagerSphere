/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - SphereApplication.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data

import android.app.Application
import com.etb.filemanager.data.deletedfiles.AppContainer
import com.etb.filemanager.data.deletedfiles.AppDataContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SphereApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}