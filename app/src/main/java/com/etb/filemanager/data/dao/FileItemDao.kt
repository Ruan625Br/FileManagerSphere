package com.etb.filemanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.etb.filemanager.data.entities.FileItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FileItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(fileItemEntity: FileItemEntity)

    @Update
    suspend fun update(fileItemEntity: FileItemEntity)

    @Delete
    suspend fun delete(fileItemEntity: FileItemEntity)

    @Query("SELECT * from file_items WHERE id = :id")
    fun getFileItem(id: Int): Flow<FileItemEntity>

    @Query("SELECT * from file_items")
    fun getFileItems(): Flow<List<FileItemEntity>>
}