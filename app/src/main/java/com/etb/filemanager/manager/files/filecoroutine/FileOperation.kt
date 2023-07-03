package com.etb.filemanager.manager.files.filecoroutine

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.*


suspend fun performFileOperation(
        context: Context,
        operation: FileOperation,
        sourcePath: List<String>,
        createDir: Boolean,
        destinationPath: String,
        progressListener: (Int) -> Unit,
        completionListener: (Boolean) -> Unit
    ){
        withContext(Dispatchers.IO) {
            try {
                when (operation){
                    FileOperation.DELETE -> deleteFile(sourcePath)
                    FileOperation.CREATE -> create(sourcePath, createDir)
                    FileOperation.RENAME -> create(sourcePath, createDir)
                    FileOperation.MOVE -> create(sourcePath, createDir)
                    FileOperation.COPY -> create(sourcePath, createDir)
                }
                completionListener(true)
            } catch (e: Exception){
                completionListener(false)
            }
        }
    }
    @OptIn(ExperimentalPathApi::class)
    private fun deleteFile(paths: List<String>){
        for (path in paths){
            val mPath = Paths.get(path)
            mPath.deleteRecursively()
        }
    }
    private fun create(paths: List<String>, dir: Boolean){
        for (path in paths){
            val mPath = Paths.get(path)
            if (!mPath.exists()){
                if (dir) mPath.createDirectory() else mPath.createFile()
            }
        }
    }


enum class FileOperation{
    DELETE,
    CREATE,
    RENAME,
    MOVE,
    COPY
}