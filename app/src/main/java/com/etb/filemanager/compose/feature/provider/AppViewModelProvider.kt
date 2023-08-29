/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - AppViewModelProvider.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.etb.filemanager.compose.feature.presentation.deletedfiles.DeletedFileDetailsViewModel
import com.etb.filemanager.compose.feature.presentation.deletedfiles.DeletedFileEntryViewModel
import com.etb.filemanager.compose.feature.presentation.deletedfiles.DeletedIFilesViewModel
import com.etb.filemanager.data.SphereApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DeletedFileEntryViewModel(SphereApplication().container.deletedFileRepository)

        }
        initializer {
            DeletedIFilesViewModel(SphereApplication().container.deletedFileRepository)
        }
        initializer {
            DeletedFileDetailsViewModel(
                SphereApplication().container.deletedFileRepository
            )
        }

    }
}
fun CreationExtras.SphereApplication(): SphereApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SphereApplication)