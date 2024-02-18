/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatSettingConverter.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.converters

import androidx.room.TypeConverter
import com.etb.filemanager.compose.core.models.ChatSettings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChatSettingsConverter {
    @TypeConverter
    fun fromChatSettings(chatSettings: ChatSettings): String {
        return Json.encodeToString(chatSettings)
    }

    @TypeConverter
    fun toChatSettings(chatSettings: String): ChatSettings {
        return Json.decodeFromString(chatSettings)
    }
}