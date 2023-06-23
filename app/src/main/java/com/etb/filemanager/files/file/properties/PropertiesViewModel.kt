package com.etb.filemanager.files.file.properties

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.etb.filemanager.manager.files.filelist.TypeOperation

class PropertiesViewModel : ViewModel() {

    private val _tabTitleLiveData = MutableLiveData<String>()
    val tabTitleLiveData: LiveData<String> = _tabTitleLiveData

    fun addNewTab(title: String) {
        _tabTitleLiveData.value = title
    }



}
