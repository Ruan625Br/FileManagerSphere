/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - MediaViewModel.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.categorylist.medialist.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etb.filemanager.files.provider.archive.common.mime.MimeType
import com.etb.filemanager.files.util.getAllMediaFromMediaStore
import com.etb.filemanager.manager.media.model.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MediaViewModel : ViewModel() {
    private val _mediaListState = MutableLiveData<List<Media>>()
    val mediaListState: LiveData<List<Media>> = _mediaListState

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading

    private val _uiState = MutableStateFlow(MediaUiState())
    val uiState: StateFlow<MediaUiState> = _uiState.asStateFlow()


    /* fun loadMediaList(context: Context, mimeType: MimeType = MimeType.ANY){
         viewModelScope.launch {
             _loading.value = true
             val mediaList = getAllMediaFromMediaStore(
                 context = context,
                 mimeType = mimeType)
             _mediaListState.value = mediaList
             _loading.value = false
         }
     }*/

    fun loadMediaList(context: Context, mimeType: MimeType = MimeType.ANY) {
      viewModelScope.launch {
          _uiState.value = MediaUiState(
              getAllMediaFromMediaStore(
                  context = context,
                  mimeType = mimeType
              )
          )
          _loading.value = false
      }
    }
}

data class MediaUiState(
    val mediaList: List<Media> = emptyList()
)