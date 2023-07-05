package com.etb.filemanager.manager.files.filelist

import android.os.AsyncTask
import android.util.Log
import com.etb.filemanager.files.provider.archive.commo.newDirectoryStream
import com.etb.filemanager.files.util.*
import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.manager.adapter.loadFileItem
import java.io.IOException
import java.nio.file.DirectoryIteratorException
import java.nio.file.Path
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import com.etb.filemanager.manager.files.filelist.DirectoryObserver

class FileListLiveData(private val path: Path) : CloseableLiveData<Stateful<List<FileModel>>>() {
    private var future: Future<Unit>? = null

    private val observer: DirectoryObserver

    @Volatile
    private var isChangedWhileInactive = false

    init {
        loadValue()
        observer = DirectoryObserver(path) { onChangeObserved() }

    }

    fun loadValue() {
        future?.cancel(true)
        value = Loading(value?.value)
        future = (AsyncTask.THREAD_POOL_EXECUTOR as ExecutorService).submit<Unit> {
            val value = try {
                path.newDirectoryStream().use { directoryStream ->
                    val fileList = mutableListOf<FileModel>()
                    for (path in directoryStream) {
                        try {
                            fileList.add(path.loadFileItem())
                        } catch (e: DirectoryIteratorException) {
                            Log.e("LIVEDATAAA", "ERRO:: $e")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    Success(fileList as List<FileModel>)
                }
            } catch (e: Exception) {
                Failure(value, e)

            }
            postValue(value as Stateful<List<FileModel>>?)
        }
    }

    private fun onChangeObserved() {
        if (hasActiveObservers()) {
            loadValue()
        } else {
            isChangedWhileInactive = true
        }
    }

    override fun onActive() {
        if (isChangedWhileInactive) {
            loadValue()
            isChangedWhileInactive = false
        }
    }

    override fun close() {
        observer.close()
        future?.cancel(true)
    }
}