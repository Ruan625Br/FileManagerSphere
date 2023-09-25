/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - DeletedIFilesViewModel.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.deletedfiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etb.filemanager.data.deletedfiles.DeletedFile
import com.etb.filemanager.data.deletedfiles.DeletedFilesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DeletedIFilesViewModel(
    deletedFilesRepository: DeletedFilesRepository
): ViewModel() {

    val deletedFilesListUiState: StateFlow<DeletedFilesListUiState> =
        deletedFilesRepository.getAllDeletedFilesStream().map { DeletedFilesListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DeletedFilesListUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class DeletedFilesListUiState(val deletedFilesList: List<DeletedFile> = listOf())