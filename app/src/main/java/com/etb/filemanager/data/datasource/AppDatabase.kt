/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - AppDatabase.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.etb.filemanager.data.converters.ChatSettingsConverter
import com.etb.filemanager.data.converters.MessagesConverter
import com.etb.filemanager.data.entities.ChatEntity

@Database(entities = [ChatEntity::class], version = 1, exportSchema = true)
@TypeConverters(ChatSettingsConverter::class, MessagesConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun chatDao(): ChatDao
}