package com.etb.filemanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.nio.file.Path

@Entity(tableName = "file_items")
data class FileItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fileData: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileItemEntity

        if (id != other.id) return false
        if (!fileData.contentEquals(other.fileData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + fileData.contentHashCode()
        return result
    }
}
