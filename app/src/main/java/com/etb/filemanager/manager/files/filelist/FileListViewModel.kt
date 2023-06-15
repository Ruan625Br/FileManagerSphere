package com.etb.filemanager.manager.files.filelist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.InterruptedIOException
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.io.path.Path
import kotlin.io.path.moveTo

class FileListViewModel : ViewModel() {
    private val TAG = "FileViewModel"

    private val _deletionProgress = MutableLiveData<Int>()
    private val _startOperation = MutableLiveData<TypeOperation>()

    val deletionProgress: LiveData<Int>
        get() = _deletionProgress

    val startOperation: LiveData<TypeOperation>
        get() = _startOperation

    fun deleteFilesAndFolders(filePaths: List<String>){
        viewModelScope.launch {
            _deletionProgress.value = 0
            for ((index, path) in filePaths.withIndex()){
                DeleteOperation.deleteFilesOrDir(path)
                _deletionProgress.value = ((index + 1) * 100) / filePaths.size
            }
            _deletionProgress.value = 100
        }
    }

    fun moveFiles(sourceFiles: List<String>, destinationDir: File){
        val executorService = Executors.newFixedThreadPool(4)

        val tasks = sourceFiles.mapIndexed { index, mFile ->
            Callable<Unit> {

                val destinationFile = Path(destinationDir.absolutePath)
                val file = Path(mFile)

                try {
                    file.moveTo(destinationFile)
                //    Files.move(file,destinationFile)
                    val progress = ((index + 1) * 100) / sourceFiles.size
                    _deletionProgress.postValue(progress)
                }catch (e: Exception){
                    Log.e(TAG, "Erro: $e")
                   /* file.moveTo(destinationFile, true)
                    val progress = ((index + 1) * 100) / sourceFiles.size
                    _deletionProgress.postValue(progress)
*/
                }


            }
        }

        try {
            executorService.invokeAll(tasks)
        }catch (e: InterruptedIOException){
            Log.e(TAG, "${e.message}")
        } finally {
            executorService.shutdown()
        }
    }

    fun startOperation(typeOperation: TypeOperation){
        _startOperation.postValue(typeOperation)
    }

    fun initOperation(typeOperation: TypeOperation, sourceFiles: List<String>, destinationDir: File){
        when(typeOperation){
            TypeOperation.CUT -> { moveFiles(sourceFiles, destinationDir)}
        }
    }

    /*fun Path.moveTo(target: Path): Path{

    }
    fun Path.moveTo(target: Path, overwrite: Boolean = false): Path{

    }*/

}

enum class TypeOperation(){
    CUT,
   // COPY,
}