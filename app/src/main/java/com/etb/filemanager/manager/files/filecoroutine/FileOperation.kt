package com.etb.filemanager.manager.files.filecoroutine

import android.content.Context
import com.etb.filemanager.R
import com.etb.filemanager.files.util.ContextUtils
import com.etb.filemanager.manager.util.MaterialDialogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InterruptedIOException
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.*


suspend fun performFileOperation(
    context: Context,
    operation: FileOperation,
    sourcePath: List<String>?,
    newNames: List<String>?,
    createDir: Boolean,
    destinationPath: String,
    progressListener: (Int) -> Unit,
    completionListener: (Boolean) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            when (operation) {
                FileOperation.DELETE -> deleteFile(sourcePath!!)
                FileOperation.CREATE -> create(sourcePath!!, createDir)
                FileOperation.RENAME -> rename(sourcePath!!, newNames!!)
                FileOperation.MOVE -> create(sourcePath!!, createDir)
                FileOperation.COPY -> create(sourcePath!!, createDir)
            }
            completionListener(true)
        } catch (e: Exception) {
            completionListener(false)
        }
    }
}

@OptIn(ExperimentalPathApi::class)
private fun deleteFile(paths: List<String>) {
    for (path in paths) {
        val mPath = Paths.get(path)
        try {
            mPath.deleteRecursively()

        } catch (e: IOException) {
            try {
                showOperationDialog()
            } catch (e: Exception) {

            }
        }
    }
}

private fun create(paths: List<String>, dir: Boolean) {
    for (path in paths) {
        val mPath = Paths.get(path)
        if (!mPath.exists()) {
            try {
                if (dir) mPath.createDirectory() else mPath.createFile()

            } catch (e: IOException) {
                try {
                    showOperationDialog()
                } catch (e: Exception) {

                }
            }
        }
    }
}

private fun rename(paths: List<String>, newNames: List<String>) {
    for ((index, mPath) in paths.withIndex()) {
        val path = Paths.get(mPath)
        val newPath = path.resolveSibling(newNames[index])
        moveAtomically(path, newPath)
    }
}

private fun moveAtomically(source: Path, target: Path) {
    try {
        source.moveTo(target, LinkOption.NOFOLLOW_LINKS, StandardCopyOption.ATOMIC_MOVE)

    } catch (e: InterruptedIOException) {
        showOperationDialog()
    }
}

private fun showOperationDialog() {
    val mContext = ContextUtils.getContext()
    val title = mContext.getString(R.string.error_occurred)
    val message = mContext.getString(R.string.error_occurred_during_operation)
    val textPositiveButton = mContext.getString(R.string.ok)

    MaterialDialogUtils().createDialogInfo(
        title,
        message,
        textPositiveButton,
        "",
        mContext,
        false
    ) { dialogResult ->
        val isConfirmed = dialogResult.confirmed
        if (isConfirmed) {

        }
    }
}


enum class FileOperation {
    DELETE,
    CREATE,
    RENAME,
    MOVE,
    COPY
}