package com.etb.filemanager.manager.files.filelist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

class FileListViewModel : ViewModel() {
    private val _deletionProgress = MutableLiveData<Int>()
    val deletionProgress: LiveData<Int>
        get() = _deletionProgress

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

}