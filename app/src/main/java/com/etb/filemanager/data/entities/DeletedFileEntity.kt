package com.etb.filemanager.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.nio.file.Path

@Entity(tableName = "deleted_files")
data class DeletedFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "file_data") val fileData: ByteArray,
    @ColumnInfo(name = "file_path") val filePath: Path,
    @ColumnInfo(name = "delete_timestamp") val deleteTimestamp: Long,
    @ColumnInfo(name = "expiration_days") val expirationDays: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeletedFileEntity

        if (id != other.id) return false
        if (!fileData.contentEquals(other.fileData)) return false
        if (filePath != other.filePath) return false
        if (deleteTimestamp != other.deleteTimestamp) return false
        if (expirationDays != other.expirationDays) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + fileData.contentHashCode()
        result = 31 * result + filePath.hashCode()
        result = 31 * result + deleteTimestamp.hashCode()
        result = 31 * result + expirationDays
        return result
    }
}
