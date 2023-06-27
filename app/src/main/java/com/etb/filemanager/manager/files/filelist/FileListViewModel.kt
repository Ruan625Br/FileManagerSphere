package com.etb.filemanager.manager.files.filelist

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etb.filemanager.R
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class FileListViewModel : ViewModel() {
    private val TAG = "FileViewModel"

    private val _operationTitle = MutableLiveData<String>()
    private val _operationMsg = MutableLiveData<String>()
    private val _operationProgress = MutableLiveData<Int>()
    private val _startOperation = MutableLiveData<TypeOperation>()
    private val _cancelOperationProgress = MutableLiveData<Unit>()

    val operationProgress: LiveData<Int>
        get() = _operationProgress

    val startOperation: LiveData<TypeOperation>
        get() = _startOperation
    val cancelOperationProgress: LiveData<Unit>
        get() = _cancelOperationProgress
    val operationTitle: LiveData<String>
        get() = _operationTitle
     val operationMsg: LiveData<String>
        get() = _operationMsg

    fun deleteFilesAndFolders(filePaths: List<String>){
        viewModelScope.launch {
            _operationProgress.value = 0
            for ((index, path) in filePaths.withIndex()){
                DeleteOperation.deleteFilesOrDir(path)
                _operationProgress.value = ((index + 1) * 100) / filePaths.size
            }
            _operationProgress.value = 100
        }
    }



    fun moveFiles(sourceFiles: List<File>, destinationDir: File, context: Context) {
        val executorService = Executors.newFixedThreadPool(4)

        val tasks = sourceFiles.mapIndexed { index, sourceFile ->
            Callable<Unit> {
                val destinationFile = destinationDir.resolve(sourceFile.name)

                try {
                    val progress = ((index + 1) * 100) / sourceFiles.size
                    val title = context.resources.getQuantityString(R.plurals.movingItems, 1, sourceFiles.size)
                    val numItems = sourceFiles.size
                    val msg = context.resources.getQuantityString(R.plurals.movingItems, numItems, numItems, sourceFile.name, destinationFile.path)
                    _operationProgress.value = progress
                    _operationTitle.value = title
                    _operationMsg.value = msg

                    Files.move(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

                } catch (e: Exception) {
                    Log.e(TAG, "Error: $e")
                }
            }
        }

        try {
            executorService.invokeAll(tasks)
            Toast.makeText(context, "Arquivos movidos com sucesso", Toast.LENGTH_LONG).show()
        } catch (e: InterruptedException) {
            Log.e(TAG, "${e.message}")
        } finally {
            _cancelOperationProgress.postValue(Unit)
            executorService.shutdown()
        }
    }

    fun startOperation(typeOperation: TypeOperation){
        _startOperation.postValue(typeOperation)
    }

    fun initOperation(typeOperation: TypeOperation, sourceFiles: List<File>, destinationDir: File, context: Context){
        when(typeOperation){
            TypeOperation.CUT -> { moveFiles(sourceFiles, destinationDir, context)}
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