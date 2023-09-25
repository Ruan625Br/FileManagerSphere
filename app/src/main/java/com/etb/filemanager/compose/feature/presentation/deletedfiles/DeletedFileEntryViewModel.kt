/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - DeletedFileEntryViewModel.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.deletedfiles

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.etb.filemanager.data.deletedfiles.DeletedFile
import com.etb.filemanager.data.deletedfiles.DeletedFilesRepository
import java.io.File

class DeletedFileEntryViewModel(
    private val deletedFilesRepository: DeletedFilesRepository): ViewModel() {

   var deletedFileUIState by mutableStateOf(DeletedFileUiState())
       private set


   fun updateUiState(deletedFileDetails: DeletedFileDetails){
       deletedFileUIState = DeletedFileUiState(
           deletedFileDetails = deletedFileDetails
       )
   }

   suspend fun saveDeletedFile(){
       deletedFilesRepository.insertDeletedFile(deletedFileUIState.deletedFileDetails.toDeletedFile())
   }
   suspend fun saveDeletedFile(deletedFileDetails: DeletedFileDetails){
       deletedFilesRepository.insertDeletedFile(deletedFileDetails.toDeletedFile())
   }
}


data class DeletedFileUiState(
    val deletedFileDetails: DeletedFileDetails = DeletedFileDetails()
)

data class DeletedFileDetails(
    val id: Int = 0,
    val fileName: String = "",
    val filePath: String = "",
    val fileData: ByteArray = "".toByteArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeletedFileDetails

        if (id != other.id) return false
        if (fileName != other.fileName) return false
        if (filePath != other.filePath) return false
        if (!fileData.contentEquals(other.fileData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + fileName.hashCode()
        result = 31 * result + filePath.hashCode()
        result = 31 * result + fileData.contentHashCode()
        return result
    }
}

fun DeletedFileDetails.toDeletedFile(): DeletedFile = DeletedFile(
    id = id,
    fileName = fileName,
    filePath = filePath,
    fileData = fileData
)

fun DeletedFile.toDeletedFileDetails(): DeletedFileDetails{

    return DeletedFileDetails(
        id = id,
        fileName = fileName,
        filePath = filePath,
        fileData = fileData
    )
}

fun DeletedFileDetails.restore(deletedFile: DeletedFile): Boolean {
    val file = File(filePath)

    if (file.exists()){
        return false
    }
    return try {
        file.writeBytes(deletedFile.fileData)
        true
    } catch (e: Exception) {
        false
    }
}
fun DeletedFile.toDeletedFileUiState(): DeletedFileUiState = DeletedFileUiState(
    deletedFileDetails = this.toDeletedFileDetails()
)

fun File.toDeletedFileDetails(): DeletedFileDetails = DeletedFileDetails(
    fileName = name,
    filePath = path,
    fileData = readBytes()
)