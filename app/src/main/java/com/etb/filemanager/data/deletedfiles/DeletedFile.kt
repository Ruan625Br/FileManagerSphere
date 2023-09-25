/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - DeletedFile.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.deletedfiles

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_files")
data class DeletedFile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fileName: String,
    val filePath: String,
    val fileData: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeletedFile

        if (id != other.id) return false
        if (fileName != other.fileName) return false
        if (filePath != other.filePath) return false
        if (!fileData.contentEquals(other.fileData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + fileName.hashCode()
        result = 31 * result + filePath.hashCode()
        result = 31 * result + fileData.contentHashCode()
        return result
    }

}