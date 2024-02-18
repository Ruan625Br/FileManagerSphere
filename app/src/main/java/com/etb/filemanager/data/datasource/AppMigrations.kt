/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - AppMigrations.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.datasource

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AppMigrations {

     val MIGRATION_1_2: Migration = object  : Migration(1, 2){
        override fun migrate(db: SupportSQLiteDatabase) {
            //No need to do anything, this is an empty migration for now
        }

    }
}