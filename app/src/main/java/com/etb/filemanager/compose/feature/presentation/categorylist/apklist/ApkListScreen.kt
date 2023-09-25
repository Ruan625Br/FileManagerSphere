/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ApkListScreen.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.categorylist.apklist

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.etb.filemanager.R
import com.etb.filemanager.compose.core.presentation.components.FilterChipGroup
import com.etb.filemanager.compose.feature.presentation.categorylist.apklist.components.ApkList
import com.etb.filemanager.compose.feature.presentation.categorylist.apklist.components.ApkListViewModel
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
                                    text = stringResource(id = R.string.apps),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }, scrollBehavior = scrollBehavior
                        )
                    },
                    content = { innerPadding ->
                        ApkListScreenBody(
                            innerPadding = innerPadding,
                        )

                    })
            }
        }
    }
}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ApkListScreenBody(
        innerPadding: PaddingValues,
    ) {
        val context = LocalContext.current

        val chipsList = remember {
            mutableStateListOf(
                context.getString(R.string.installed_apps) to true,
                context.getString(R.string.system_apps) to false,
                context.getString(R.string.app_installation_files) to false,
            )
        }
        var chipSelectedIndex by remember { mutableIntStateOf(0) }
        var updateAppList by remember { mutableStateOf(false) }


        Column(
        ) {
            FilterChipGroup(innerPadding = innerPadding,
                modifier = Modifier
                    .padding(start = 10.dp, bottom = 5.dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp)),
                chips = chipsList,
                onChipClick = { index ->
                    chipsList[index] = chipsList[index].copy(second = true)
                    chipSelectedIndex = index
                    updateAppList = true
                    chipsList.indices.forEachIndexed { i, _ ->
                        if (i != index) {

                            chipsList[i] = chipsList[i].copy(second = false)

                        }
                    }

                })

            LaunchedEffect(chipSelectedIndex) {
                updateAppList = true

            }


            if (updateAppList) {
                GeneratingApkListBase(index = chipSelectedIndex)
            }
        }

    }

    @Composable
    fun GeneratingApkList(
        innerPadding: PaddingValues,
        appFilter: AppFilter = AppFilter.ALL,
        apkListViewModel: ApkListViewModel = viewModel(),

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


    @Composable
    fun GeneratingApkListBase(index: Int) {
        val apkListViewModel: ApkListViewModel = viewModel()
        val context = LocalContext.current

        val appFilter: AppFilter = when (index) {
            0 -> AppFilter.NON_SYSTEM
            1 -> AppFilter.SYSTEM
            2 -> AppFilter.UNINSTALLED_INTERNAL
            else -> {
                AppFilter.NON_SYSTEM
            }
        }
        apkListViewModel.update(context, appFilter)

        GeneratingApkList(
            innerPadding = PaddingValues(0.dp),
            appFilter = appFilter,
            apkListViewModel = apkListViewModel
        )

    }
