package com.etb.filemanager.manager.files.filecoroutine

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etb.filemanager.R
import com.etb.filemanager.manager.files.filelist.DeleteOperation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InterruptedIOException
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.deleteIfExists
import kotlin.io.path.moveTo

class FileCoroutineViewModel : ViewModel() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _operationInfo = MutableLiveData<FileOperationInfo>()
    val operationInfo: LiveData<FileOperationInfo>
        get() = _operationInfo

    private val _operationTitle = MutableLiveData<String>()
    private val _operationMsg = MutableLiveData<String>()
    private val _operationProgress = MutableLiveData<Int>()
    private val _cancelOperationProgress = MutableLiveData<Unit>()

    val operationProgress: LiveData<Int>
        get() = _operationProgress

    val cancelOperationProgress: LiveData<Unit>
        get() = _cancelOperationProgress
    val operationTitle: LiveData<String>
        get() = _operationTitle
    val operationMsg: LiveData<String>
        get() = _operationMsg

    fun deleteFilesAndFolders(filePaths: List<String>) {
        viewModelScope.launch {
            _operationProgress.value = 0
            for ((index, path) in filePaths.withIndex()) {
                DeleteOperation.deleteFilesOrDir(path)
                _operationProgress.value = ((index + 1) * 100) / filePaths.size
            }
            _operationProgress.value = 100
        }
    }

    fun delete(paths: List<Path>) {
        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    for (path in paths) {
                        delete(path)
                    }
                }
            } catch (e: Exception) {
                Log.e("DLEEEEEE", "ERrro: $e")
            }
        }
    }

    fun rename(paths: List<Path>, newNames: List<String>, context: Context) {
        val totalFiles = paths.size
        var completedFiles = 0

        viewModelScope.launch(Dispatchers.IO) {
            try {
                paths.forEachIndexed { index, path ->
                    val newName = newNames[index]
                    val newPath = path.resolveSibling(newName)
                    moveAtomically(path, newPath)
                    val msg = context.resources.getQuantityString(
                        R.plurals.files_files_renaming_msg_count_format, paths.size, path.fileName, newPath
                    )

                    completedFiles++
                    val progress = (completedFiles.toFloat() / totalFiles.toFloat()) * 100
                    val title = context.resources.getQuantityString(
                        R.plurals.files_files_renaming_count_format, totalFiles
                    )
                    val fileOperationInfo = FileOperationInfo(
                        title = title,
                        message = msg,
                        progress = if (progress < 100) progress.toInt() else 50
                    )

                    withContext(Dispatchers.Main) {
                        _operationInfo.value = fileOperationInfo
                    }
                }
            } catch (e: Exception) {
                Log.e("DLEEEEEE", "ERrro: $e")
            }
        }


    }

    @Throws(IOException::class)
    private fun moveAtomically(source: Path, target: Path) {
        source.moveTo(target, LinkOption.NOFOLLOW_LINKS, StandardCopyOption.ATOMIC_MOVE)
    }

    @Throws(IOException::class)
    private fun delete(path: Path) {
        var retry: Boolean
        do {
            retry = try {
                path.deleteIfExists()
                false
            } catch (e: InterruptedIOException) {
                throw e
            } catch (e: IOException) {
                true
            }
        } while (retry)
    }
}

data class FileOperationInfo(
    val title: String,
    val message: String,
    val progress: Int?,
)




