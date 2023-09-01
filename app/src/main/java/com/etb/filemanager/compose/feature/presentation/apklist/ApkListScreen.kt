/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ApkListScreen.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.apklist

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.etb.filemanager.compose.feature.presentation.apklist.components.ApkList
import com.etb.filemanager.compose.feature.presentation.apklist.components.ApkListViewModel
import com.etb.filemanager.compose.feature.provider.BaseScreen
import com.etb.filemanager.files.extensions.AppFilter
import com.etb.filemanager.ui.theme.FileManagerTheme


class ApkListScreen : BaseScreen() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

            FileManagerTheme {

                Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            title = {
                                Text(
                                    "Apps", maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            }, scrollBehavior = scrollBehavior
                        )
                    },
                    content = { innerPadding ->
                        ApkListScreenBody(innerPadding = innerPadding)

                    })
            }
        }
    }


    @Composable
    fun ApkListScreenBody(
        innerPadding: PaddingValues
    ) {

        GeneratingApkList(innerPadding = innerPadding)

    }

    @Composable
    fun GeneratingApkList(
        innerPadding: PaddingValues,
        appFilter: AppFilter = AppFilter.ALL,
        apkListViewModel: ApkListViewModel = viewModel()

    ) {
        val context = LocalContext.current

        val apkListState by apkListViewModel.apkListState.observeAsState(emptyList())
        val loading by apkListViewModel.loading.observeAsState(true)

        LaunchedEffect(context) {
            apkListViewModel.loadApkList(context, appFilter)
        }

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator()
            }
        } else {
            ApkList(innerPadding = innerPadding, apkList = apkListState, appFilter = appFilter)
        }
    }


}