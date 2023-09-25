/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - AppContainer.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.deletedfiles

import android.content.Context

interface AppContainer {
    val deletedFileRepository: DeletedFilesRepository
}

class AppDataContainer(private val context: Context) : AppContainer{
    override val deletedFileRepository: DeletedFilesRepository by lazy {
        OfflineDeletedFilesRepository(AppDatabase.getDatabase(context).deletedFileDao())
    }

}