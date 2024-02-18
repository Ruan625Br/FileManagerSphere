/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - MessagesConverter.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.converters

import androidx.room.TypeConverter
import com.etb.filemanager.compose.core.models.Message
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MessagesConverter {
    @TypeConverter
    fun fromMessages(messages: List<Message>): String {
        return  Json.encodeToString(messages)
    }
    @TypeConverter
    fun toMessages(messages: String): List<Message> {
        return Json.decodeFromString(messages)
    }
}