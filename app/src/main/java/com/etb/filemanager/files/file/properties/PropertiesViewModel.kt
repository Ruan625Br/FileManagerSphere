package com.etb.filemanager.files.file.properties

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.etb.filemanager.manager.files.filelist.TypeOperation

class PropertiesViewModel : ViewModel() {
    private val _fragment = MutableLiveData<Fragment>()
    private val _startOperation = MutableLiveData<TypeOperation>()

    val fragment: LiveData<Fragment>
        get() = _fragment



    val startOperation: LiveData<TypeOperation>
        get() = _startOperation



}
