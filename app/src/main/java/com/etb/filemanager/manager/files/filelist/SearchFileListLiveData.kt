package com.etb.filemanager.manager.files.filelist

import android.os.AsyncTask
import android.util.Log
import com.etb.filemanager.files.provider.archive.commo.search
import com.etb.filemanager.files.util.CloseableLiveData
import com.etb.filemanager.files.util.Failure
import com.etb.filemanager.files.util.Loading
import com.etb.filemanager.files.util.Stateful
import com.etb.filemanager.files.util.Success
import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.manager.adapter.loadFileItem
import java.io.IOException
import java.nio.file.Path
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class SearchFileListLiveData(
    private val path: Path,
    private val query: String
) : CloseableLiveData<Stateful<List<FileModel>>>() {
    private var future: Future<Unit>? = null

    init {
        loadValue()
    }

    fun loadValue() {
        future?.cancel(true)
        value = Loading(emptyList())
        future = (AsyncTask.THREAD_POOL_EXECUTOR as ExecutorService).submit<Unit> {
            val fileList = mutableListOf<FileModel>()
            try {
                path.search(query, INTERVAL_MILLIS) { paths: List<Path> ->
                    for (path in paths) {
                        val fileItem = try {
                            path.loadFileItem()
                        } catch (e: IOException) {
                            Log.e("WEER", "ERRO: $e")

                            e.printStackTrace()
                            continue
                        }
                        fileList.add(fileItem)
                    }
                    postValue(Loading(fileList.toList()))
                }
                postValue(Success(fileList))
            } catch (e: Exception) {
                Log.e("WEER", "ERRO: $e")
                Failure(value, e)
            }
        }
    }

    override fun close() {
        future?.cancel(true)
    }

    companion object {
        private const val INTERVAL_MILLIS = 500L
    }
}
class SearchState(val isSearching: Boolean, val query: String)
