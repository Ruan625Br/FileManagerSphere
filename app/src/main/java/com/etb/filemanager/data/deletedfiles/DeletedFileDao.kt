/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - DeletedFileDao.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.deletedfiles

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.etb.filemanager.manager.adapter.FileModel
import kotlinx.coroutines.flow.Flow

@Dao
interface DeletedFileDao {
    @Query("SELECT * from deleted_files ORDER BY fileName ASC")
    fun getAllDeletedFiles(): Flow<List<DeletedFile>>

    @Query("SELECT * from deleted_files WHERE id = :id")
    fun getDeletedFile(id: Int): Flow<DeletedFile>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deletedFile: DeletedFile)

    @Update
    suspend fun update(deletedFile: DeletedFile)

    @Delete
    suspend fun delete(deletedFile: DeletedFile)
}