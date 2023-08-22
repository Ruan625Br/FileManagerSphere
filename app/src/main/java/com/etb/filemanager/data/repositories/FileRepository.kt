package com.etb.filemanager.data.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.etb.filemanager.data.dao.DeletedFileDao
import com.etb.filemanager.data.entities.DeletedFileEntity
import java.nio.file.Path
import java.util.concurrent.TimeUnit

class FileRepository(private val deletedFileDao: DeletedFileDao) {
    val allDeletedFiles: LiveData<List<DeletedFileEntity>> = deletedFileDao.getAllDeletedFiles()

    suspend fun deleteFile(context: Context, path: Path, expirationDays: Int) {
        // Delete the file from storage
        val file = path.toFile()
        if (file.exists()) {
            // Store the deleted file in the database
            val fileData = file.readBytes()
            val deleteTimestamp = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(expirationDays.toLong())
            val deletedFile = DeletedFileEntity(
                fileData = fileData,
                filePath = path,
                deleteTimestamp = deleteTimestamp,
                expirationDays = expirationDays
            )
            deletedFileDao.insertDeletedFile(deletedFile)
            file.delete()
        }
    }

    suspend fun restoreFile(context: Context, path: Path) {
        // Retrieve the deleted file from the database
        val deletedFile = deletedFileDao.getDeletedFileByPath(path)
        deletedFile?.let {
            // Restore the file to storage
            val restoredFile = it.filePath.toFile()
            restoredFile.writeBytes(it.fileData)
            // Delete the entry from the database
            deletedFileDao.deleteDeletedFile(it)
        }
    }
}