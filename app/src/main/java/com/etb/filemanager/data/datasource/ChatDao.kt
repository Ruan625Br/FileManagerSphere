/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatDao.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.etb.filemanager.data.entities.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Upsert
    suspend fun upsert(chatEntity: ChatEntity)
    @Query("SELECT * FROM ChatEntity")
    fun getAllChat(): Flow<List<ChatEntity>>
    @Delete
    suspend fun delete(chatEntity: ChatEntity)
}