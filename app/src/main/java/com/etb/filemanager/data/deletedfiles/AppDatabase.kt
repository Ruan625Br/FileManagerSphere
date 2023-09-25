/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - AppDatabase.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.deletedfiles

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DeletedFile::class], version =1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deletedFileDao(): DeletedFileDao

    companion object {

        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}