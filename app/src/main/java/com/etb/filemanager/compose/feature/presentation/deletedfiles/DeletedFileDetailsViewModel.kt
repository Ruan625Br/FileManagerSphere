/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - DeletedFileDetailsViewModel.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.deletedfiles

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etb.filemanager.data.deletedfiles.DeletedFile
import com.etb.filemanager.data.deletedfiles.DeletedFilesRepository
import com.etb.filemanager.manager.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.nio.file.Paths
import kotlin.io.path.createFile
import kotlin.io.path.exists

class DeletedFileDetailsViewModel(
    private val deletedFilesRepository: DeletedFilesRepository
): ViewModel() {

    var deletedFileId: Int = 0

    val uiState: StateFlow<DeletedFileDetailsUiState> =
        deletedFilesRepository.getDeletedFileStream(deletedFileId)
            .filterNotNull()
            .map {
                DeletedFileDetailsUiState(deletedFileDetails = it.toDeletedFileDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DeletedFileDetailsUiState()
            )



    suspend fun deleteFileFromDatabase(deletedFile: DeletedFile){
        deletedFilesRepository.deleteFileFromDatabase(deletedFile)
    }
     fun restoreDeletedFile(deletedFile: DeletedFile): Boolean{
        val deletedFileDetails = deletedFile.toDeletedFileDetails()

        return deletedFileDetails.restore(deletedFile = deletedFile)

    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class DeletedFileDetailsUiState(
    val deletedFileDetails: DeletedFileDetails = DeletedFileDetails()
)