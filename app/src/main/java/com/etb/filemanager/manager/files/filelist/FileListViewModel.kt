package com.etb.filemanager.manager.files.filelist

import android.os.Parcelable
import androidx.lifecycle.*
import com.etb.filemanager.files.util.CloseableLiveData
import com.etb.filemanager.files.util.Stateful
import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.settings.preference.Preferences
import java.io.Closeable
import java.nio.file.Path
import java.nio.file.Paths


class FileListViewModel : ViewModel() {
    private val TAG = "FileViewModel"

    private val trailLiveData = TrailLiveData()
    val hasTrail: Boolean
        get() = trailLiveData.value != null
    val pendingState: Parcelable?
        get() = trailLiveData.value?.pendigSate


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
    fun navigateUp(): Boolean =
        if (currentPath != Paths.get(Preferences.Behavior.getDefaultFolder())) trailLiveData.navigateUp() else false

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
            val liveData = FileListLiveData(path!!)


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


