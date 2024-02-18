/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatMapper.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.mapper

import com.etb.filemanager.compose.core.models.Chat
import com.etb.filemanager.data.entities.ChatEntity


fun ChatEntity.toChat() = Chat(
    id = id,
    chatSettings = chatSettings,
    messages = messages
)
fun Chat.toChatEntity() = ChatEntity(
    id = id,
    chatSettings = chatSettings,
    messages = messages
)