package com.etb.filemanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.etb.filemanager.data.entities.DeletedFileEntity
import java.nio.file.Path

interface DeletedFileDao {
    @Insert
    suspend fun insertDeletedFile(deletedFile: DeletedFileEntity)

    @Query("SELECT * FROM deleted_files WHERE file_path = :filePath")
    suspend fun getDeletedFileByPath(filePath: Path): DeletedFileEntity?

    @Query("SELECT * FROM deleted_files")
    fun getAllDeletedFiles(): LiveData<List<DeletedFileEntity>>

    @Delete
    suspend fun deleteDeletedFile(deletedFile: DeletedFileEntity)
}