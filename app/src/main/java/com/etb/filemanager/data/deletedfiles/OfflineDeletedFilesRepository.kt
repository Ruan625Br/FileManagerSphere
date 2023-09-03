/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - OfflineDeletedFilesRepository.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.deletedfiles

import kotlinx.coroutines.flow.Flow

class OfflineDeletedFilesRepository(private val deletedFileDao: DeletedFileDao) : DeletedFilesRepository {
    override fun getAllDeletedFilesStream(): Flow<List<DeletedFile>> = deletedFileDao.getAllDeletedFiles()
    override fun getDeletedFileStream(id: Int): Flow<DeletedFile?> = deletedFileDao.getDeletedFile(id)
    override suspend fun insertDeletedFile(deletedFile: DeletedFile) = deletedFileDao.insert(deletedFile)
    override suspend fun deleteFileFromDatabase(deletedFile: DeletedFile) = deletedFileDao.delete(deletedFile)
    override suspend fun updateDeletedFile(deletedFile: DeletedFile) = deletedFileDao.update(deletedFile)
}