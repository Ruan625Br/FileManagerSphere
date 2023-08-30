package com.etb.filemanager.files.provider.archive.common.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PropertiesViewModel : ViewModel() {

    private val _tabTitleLiveData = MutableLiveData<String>()
    val tabTitleLiveData: LiveData<String> = _tabTitleLiveData

    fun addNewTab(title: String) {
        _tabTitleLiveData.value = title
    }


}
