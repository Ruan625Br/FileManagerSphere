/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - DeletedFilesRepository.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.deletedfiles

import kotlinx.coroutines.flow.Flow

interface DeletedFilesRepository {

    fun getAllDeletedFilesStream(): Flow<List<DeletedFile>>

    fun getDeletedFileStream(id: Int): Flow<DeletedFile?>

    suspend fun insertDeletedFile(deletedFile: DeletedFile)

    suspend fun deleteFileFromDatabase(deletedFile: DeletedFile)

    suspend fun updateDeletedFile(deletedFile: DeletedFile)
}