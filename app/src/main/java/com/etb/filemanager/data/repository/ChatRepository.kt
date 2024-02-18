/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatRepository.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.repository

import com.etb.filemanager.data.entities.ChatEntity
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun upsert(chat: ChatEntity)

    suspend fun getAllChat(): Flow<List<ChatEntity>>

    suspend fun delete(chat: ChatEntity)
}