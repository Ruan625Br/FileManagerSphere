/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatEntity.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.etb.filemanager.compose.core.models.ChatSettings
import com.etb.filemanager.compose.core.models.Message
import com.etb.filemanager.data.converters.ChatSettingsConverter
import com.etb.filemanager.data.converters.MessagesConverter

@Entity
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @TypeConverters(ChatSettingsConverter::class)
    val chatSettings: ChatSettings = ChatSettings(),
    @TypeConverters(MessagesConverter::class)
    val messages: List<Message> = emptyList()
)
