/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatRepositoryImpl.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.repository

import com.etb.filemanager.data.datasource.ChatDao
import com.etb.filemanager.data.entities.ChatEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao
): ChatRepository {

    override suspend fun upsert(chat: ChatEntity) = chatDao.upsert(chat)
    override suspend fun getAllChat(): Flow<List<ChatEntity>> = chatDao.getAllChat()

    override suspend fun delete(chat: ChatEntity) = chatDao.delete(chat)
}