package com.etb.filemanager.manager.files.filelist

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
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
        if (currentPath != Paths.get(Preferences.Behavior.defaultFolder)) trailLiveData.navigateUp() else false

    val currentPathLiveData = trailLiveData.map { it.currentPath }

    val currentPath: Path?
        get() = currentPathLiveData.value

    private val _searchStateLiveData = MutableLiveData(SearchState(false, ""))
    val searchStateLive: LiveData<SearchState> = _searchStateLiveData
    val searchState: SearchState
        get() = _searchStateLiveData.value!!

    fun search(query: String) {
        val searchState = _searchStateLiveData.value!!
        if (searchState.isSearching && searchState.query == query) return
        _searchStateLiveData.value = SearchState(true, query)
    }

    fun stopSearching() {
        val searchState = _searchStateLiveData.value!!
        if (!searchState.isSearching) return
        _searchStateLiveData.value = SearchState(false, "")
    }


    private val _fileListLiveData =
        FileListSwitchMapLiveData(currentPathLiveData, _searchStateLiveData)
    val fileListLiveData: LiveData<Stateful<List<FileModel>>>
        get() = _fileListLiveData
    val fileListStateful: Stateful<List<FileModel>>
        get() = _fileListLiveData.value!!

    fun reload() {
        val path = currentPath
        _fileListLiveData.reload()
    }


    val searchViewExpandedLiveData = MutableLiveData(false)
    var isSearchViewExpanded: Boolean
        get() = searchViewExpandedLiveData.value!!
        set(value) {
            if (searchViewExpandedLiveData.value == value) return
            searchViewExpandedLiveData.value = value
        }
    private val _searchViewQueryLiveData = MutableLiveData("")
    var searchViewQuery: String
        get() = _searchViewQueryLiveData.value!!
        set(value) {
            if (_searchViewQueryLiveData.value == value) return
            _searchViewQueryLiveData.value = value
        }

    private class FileListSwitchMapLiveData(
        private val pathLiveData: LiveData<Path>,
        private val searchStateLiveData: LiveData<SearchState>
    ) : MediatorLiveData<Stateful<List<FileModel>>>(), Closeable {
        private var liveData: CloseableLiveData<Stateful<List<FileModel>>>? = null

        init {
            addSource(pathLiveData) { updateSource() }
            addSource(searchStateLiveData) { updateSource() }
        }

        private fun updateSource() {
            liveData?.let {
                removeSource(it)
                it.close()
            }
            val path = pathLiveData.value
            val searchState = searchStateLiveData.value!!
            val liveData = if (searchState.isSearching) SearchFileListLiveData(
                path!!,
                searchState.query
            ) else FileListLiveData(path!!)


            this.liveData = liveData
            addSource(liveData) { value = it }
        }

        fun reload() {
            when (val liveData = liveData) {
                is FileListLiveData -> liveData.loadValue()
                is SearchFileListLiveData -> liveData.loadValue()
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

    private val _sortOptionsLiveData = MutableLiveData<Unit>()
    private val _showHiddenFilesLiveData = MutableLiveData<Unit>()
    private val _toggleGridLiveData = MutableLiveData<Boolean>()
    val showHiddenFilesLiveData: LiveData<Unit> = _showHiddenFilesLiveData
    val sortOptionsLiveData: LiveData<Unit> = _sortOptionsLiveData
    val toggleGridLiveData: LiveData<Boolean> = _toggleGridLiveData

    fun setSortBy(sortBy: FileSortOptions.SortBy) {
        Preferences.Popup.sortBy = sortBy
        _sortOptionsLiveData.value = Unit

    }

    fun setOrderFiles() {
        val currentOrder = Preferences.Popup.orderFiles
        val newOrder =
            if (currentOrder == FileSortOptions.Order.ASCENDING) FileSortOptions.Order.DESCENDING else FileSortOptions.Order.ASCENDING
        Preferences.Popup.orderFiles = newOrder
        _sortOptionsLiveData.value = Unit
    }

    fun setDirectoriesFirst() {
        Preferences.Popup.isDirectoriesFirst = !Preferences.Popup.isDirectoriesFirst
        _sortOptionsLiveData.value = Unit
    }

    fun setShowHiddenFiles(value: Boolean) {
        Preferences.Popup.showHiddenFiles = value
        _showHiddenFilesLiveData.value = Unit

    }

    fun setGriToggle() {
        val isGridEnabled = !Preferences.Popup.isGridEnabled
        Preferences.Popup.isGridEnabled = isGridEnabled
        _toggleGridLiveData.value = isGridEnabled
    }

}

enum class TypeOperation {
    CUT,
    // COPY,
}


