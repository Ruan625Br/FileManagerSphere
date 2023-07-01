package com.etb.filemanager.manager.files.filelist

import android.content.Context
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.etb.filemanager.R
import com.etb.filemanager.files.util.CloseableLiveData
import com.etb.filemanager.files.util.Stateful
import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.manager.util.MaterialDialogUtils
import kotlinx.coroutines.*
import java.io.Closeable
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption


class FileListViewModel : ViewModel() {
    private val TAG = "FileViewModel"

    private val trailLiveData = TrailLiveData()
    val hasTrail: Boolean
        get() = trailLiveData.value != null
    val pendingState: Parcelable?
        get() = trailLiveData.value?.pendigSate


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


    /*
        @OptIn(DelicateCoroutinesApi::class)
        fun moveFiles(sourceFiles: List<File>, destinationDir: File, context: Context) {
            viewModelScope.launch {
                val totalFiles = sourceFiles.size
                var completedFiles = 0


                val tasks = sourceFiles.map { sourceFile ->
                    async(Dispatchers.IO) {
                        val destinationFile = destinationDir.resolve(sourceFile.name)
                        Log.i(TAG, "Path:: ${sourceFile.path}")

                        try {
                            completedFiles++
                            val progress = (completedFiles.toFloat() / totalFiles.toFloat()) * 100
                            val title = context.resources.getQuantityString(R.plurals.movingItems, 1, sourceFiles.size)
                            val numItems = sourceFiles.size
                            val msg = context.resources.getQuantityString(
                                R.plurals.movingItems,
                                numItems,
                                numItems,
                                sourceFile.name,
                                destinationFile.path
                            )

                            withContext(Dispatchers.Main) {
                                _operationTitle.value = title
                                _operationMsg.value = msg
                                _operationProgress.value = progress.toInt()
                            }

                            Files.move(
                                sourceFile.toPath(),
                                destinationFile.toPath()
                            )
                        } catch (e: Exception) {
                            if (e is java.nio.file.FileAlreadyExistsException){
                                try {
                                    val title = context.resources.getQuantityString(R.plurals.plurals_file_already_exists,1)
                                    val msg = context.resources.getQuantityString(R.plurals.plurals_file_already_exists, 2, sourceFile.name)
                                    val textPositiveButton = context.getString(R.string.replace)
                                    val textNegativeButton = context.getString(R.string.skip)

                                    withContext(Dispatchers.Main){
                                    MaterialDialogUtils().createDialogInfo(
                                        title, msg, textPositiveButton, textNegativeButton, context, true
                                    ) { dialogResult ->
                                        val isConfirmed = dialogResult.confirmed
                                        val currentFile = sourceFile
                                        if (isConfirmed) {
                                            launch(Dispatchers.IO) {
                                                try {
                                                    withContext(Dispatchers.Main) {
                                                        val progress = (completedFiles.toFloat() / totalFiles.toFloat()) * 100
                                                        _operationTitle.value = title
                                                        _operationMsg.value = "\"msg\""
                                                        _operationProgress.value = progress.toInt()
                                                    }
                                                    Files.move(
                                                        sourceFile.toPath(),
                                                        destinationFile.toPath(),
                                                        StandardCopyOption.REPLACE_EXISTING
                                                    )
                                                } catch (e: Exception) {
                                                    Log.e(TAG, "Error 2: $e")

                                                }
                                            }
                                        }
                                        val listFiles = sourceFiles.toMutableList()
                                        if (!listFiles.isEmpty()) {
                                            listFiles.remove(currentFile)
                                            moveFiles(listFiles.toList(), destinationFile, context)
                                        }

                                    }
                                    }
                                } catch (e: Exception){
                                    Log.e(TAG, "Error 1: $e")
                                }
                            }
                            Log.e(TAG, "Error: $e")
                        }
                    }
                }

                try {
                    awaitAll(*tasks.toTypedArray())
                    withContext(Dispatchers.Main) {
                        _cancelOperationProgress.value = Unit
                        Toast.makeText(context, "Arquivos movidos com sucesso", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error: $e")
                } finally {
                    withContext(Dispatchers.Main) {
                        _cancelOperationProgress.value = Unit
                    }
                }
            }
        }
    */

    @OptIn(DelicateCoroutinesApi::class)
    fun moveFiles(sourceFiles: List<File>, destinationDir: File, context: Context) {
        viewModelScope.launch {
            val totalFiles = sourceFiles.size
            var completedFiles = 0

            val tasks = sourceFiles.map { sourceFile ->
                async(Dispatchers.IO) {
                    val destinationFile = destinationDir.resolve(sourceFile.name)
                    Log.i(TAG, "Path:: ${sourceFile.path}")

                    try {
                        completedFiles++
                        val progress = (completedFiles.toFloat() / totalFiles.toFloat()) * 100
                        val title = context.resources.getQuantityString(R.plurals.movingItems, 1, sourceFiles.size - 1)
                        val numItems = sourceFiles.size - 1
                        val msg = context.resources.getQuantityString(
                            R.plurals.movingItems, numItems, numItems, sourceFile.name, destinationFile.path
                        )

                        withContext(Dispatchers.Main) {
                            _operationTitle.value = title
                            _operationMsg.value = msg
                            _operationProgress.value = progress.toInt()
                        }

                        if (sourceFile.isDirectory) {
                            Files.move(
                                sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING
                            )
                        } else {
                            Files.move(sourceFile.toPath(), destinationFile.toPath())
                        }
                    } catch (e: Exception) {
                        if (e is java.nio.file.FileAlreadyExistsException) {
                            // Tratamento para arquivo ou diretório já existente
                            try {
                                val title =
                                    context.resources.getQuantityString(R.plurals.plurals_file_already_exists, 1)
                                val msg = context.resources.getQuantityString(
                                    R.plurals.plurals_file_already_exists, 2, sourceFile.name
                                )
                                val textPositiveButton = context.getString(R.string.replace)
                                val textNegativeButton = context.getString(R.string.skip)

                                withContext(Dispatchers.Main) {
                                    MaterialDialogUtils().createDialogInfo(
                                        title, msg, textPositiveButton, textNegativeButton, context, true
                                    ) { dialogResult ->
                                        val isConfirmed = dialogResult.confirmed
                                        val currentFile = sourceFile
                                        if (isConfirmed) {
                                            launch(Dispatchers.IO) {
                                                try {
                                                    withContext(Dispatchers.Main) {
                                                        val progress =
                                                            (completedFiles.toFloat() / totalFiles.toFloat()) * 100
                                                        _operationTitle.value = title
                                                        _operationMsg.value = "\"msg\""
                                                        _operationProgress.value = progress.toInt()
                                                    }
                                                    if (sourceFile.isDirectory) {
                                                        Files.move(
                                                            sourceFile.toPath(),
                                                            destinationFile.toPath(),
                                                            StandardCopyOption.REPLACE_EXISTING
                                                        )
                                                    } else {
                                                        Files.move(
                                                            sourceFile.toPath(),
                                                            destinationFile.toPath(),
                                                            StandardCopyOption.REPLACE_EXISTING
                                                        )
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e(TAG, "Error 2: $e")
                                                }
                                            }
                                        }
                                        val listFiles = sourceFiles.toMutableList()
                                        if (!listFiles.isEmpty()) {
                                            listFiles.remove(currentFile)
                                            moveFiles(listFiles.toList(), destinationFile, context)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error 1: $e")
                            }
                        }
                        Log.e(TAG, "Error: $e")
                    }
                }
            }

            try {
                awaitAll(*tasks.toTypedArray())
                withContext(Dispatchers.Main) {
                    _cancelOperationProgress.value = Unit
                    Toast.makeText(context, "Arquivos movidos com sucesso", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error: $e")
            } finally {
                withContext(Dispatchers.Main) {
                    _cancelOperationProgress.value = Unit
                }
            }
        }
    }


    fun startOperation(typeOperation: TypeOperation) {
        _startOperation.postValue(typeOperation)
    }

    fun initOperation(typeOperation: TypeOperation, sourceFiles: List<File>, destinationDir: File, context: Context) {
        when (typeOperation) {
            TypeOperation.CUT -> {
                moveFiles(sourceFiles, destinationDir, context)
            }
        }
    }

    private val _selectedFilesLiveData = MutableLiveData(fileItemSetOf())
    val selectedFilesLiveData: LiveData<FileItemSet>
        get() = _selectedFilesLiveData
    val selectedFiles: FileItemSet
        get() = _selectedFilesLiveData.value!!

    fun selectFile(file: FileModel, selected: Boolean) {
        selectFiles(fileItemSetOf(file), selected)
    }

    fun selectFiles(files: FileItemSet, selected: Boolean) {
        val selectedFiles = _selectedFilesLiveData.value
        if (selectedFiles === files) {
            if (!selected && selectedFiles.isNotEmpty()) {
                selectedFiles.clear()
            }
            return
        }
        var changed = false
        for (file in files) {
            changed = changed or if (selected) {
                selectedFiles!!.add(file)
            } else {
                selectedFiles!!.remove(file)
            }
        }
        if (changed) {
            _selectedFilesLiveData.postValue(selectedFiles)
        }
    }

    fun clearSelectedFiles() {
        val selectedFiles = _selectedFilesLiveData.value
        if (selectedFiles!!.isEmpty()) {
            return
        }
        selectedFiles.clear()
        _selectedFilesLiveData.postValue(selectedFiles)
    }

    fun replaceSelectedFiles(files: FileItemSet) {
        val selectedFiles = _selectedFilesLiveData.value
        if (selectedFiles == files) {
            return
        }
        selectedFiles?.clear()
        selectedFiles?.addAll(files)
        _selectedFilesLiveData.value = selectedFiles

    }

    private val _pickOptionsLiveData = MutableLiveData<PickOptions?>()
    val pickOptionsLiveData: LiveData<PickOptions?>
        get() = _pickOptionsLiveData
    var pickOptions: PickOptions?
        get() = _pickOptionsLiveData.value
        set(value) {
            _pickOptionsLiveData.value = value
        }


    fun navigateTo(lastState: Parcelable, path: Path) = trailLiveData.navigateTo(lastState, path)
    fun resetTo(path: Path) = trailLiveData.resetTo(path)
    fun navigateUp(): Boolean = trailLiveData.navigateUp()

    val currentPathLiveData = trailLiveData.map { it.currentPath }

    val currentPath: Path?
        get() = currentPathLiveData.value

    private val _fileListLiveData = FileListSwitchMapLiveData(currentPathLiveData)
    val fileListLiveData: LiveData<Stateful<List<FileModel>>>
        get() = _fileListLiveData
    val fileListStateful: Stateful<List<FileModel>>
        get() = _fileListLiveData.value!!

    fun reload() {
        val path = currentPath
        _fileListLiveData.reload()
    }
    private class FileListSwitchMapLiveData(
        private val pathLiveData: LiveData<Path>
    ) : MediatorLiveData<Stateful<List<FileModel>>>(), Closeable {
        private var liveData: CloseableLiveData<Stateful<List<FileModel>>>? = null

        init {
            addSource(pathLiveData) {
                updateSource()
            }
        }
        private fun updateSource() {
            liveData?.let {
                removeSource(it)
                it.close()
            }
            val path = pathLiveData.value
            val liveData =  FileListLiveData(path!!)


            this.liveData = liveData
            addSource(liveData) { value = it }
        }

        fun reload() {
            when (val liveData = liveData) {
                is FileListLiveData -> liveData.loadValue()
            }
        }

        override fun close() {
            liveData?.let {
                removeSource(it)
                it.close()
                this.liveData = null
            }
        }
    }
}

enum class TypeOperation() {
    CUT,
    // COPY,
}


