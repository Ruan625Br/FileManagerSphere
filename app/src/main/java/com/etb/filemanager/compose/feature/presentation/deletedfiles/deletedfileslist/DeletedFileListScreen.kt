/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - DeletedFileListScreen.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.deletedfiles.deletedfileslist

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.etb.filemanager.R
import com.etb.filemanager.compose.feature.presentation.deletedfiles.DeletedFileDetailsViewModel
import com.etb.filemanager.compose.feature.presentation.deletedfiles.DeletedFileEntryViewModel
import com.etb.filemanager.compose.feature.presentation.deletedfiles.DeletedIFilesViewModel
import com.etb.filemanager.compose.feature.presentation.deletedfiles.toDeletedFileDetails
import com.etb.filemanager.compose.feature.presentation.deletedfiles.deletedfileslist.components.BottomSheetInfo
import com.etb.filemanager.compose.feature.provider.AppViewModelProvider
import com.etb.filemanager.compose.feature.provider.BaseScreen
import com.etb.filemanager.data.deletedfiles.DeletedFile
import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.ui.theme.FileManagerTheme
import com.etb.filemanager.ui.theme.Shapes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
class DeletedFileListScreen : BaseScreen() {
    private val TAG = "FileListScreen"

    private var fileModel: FileModel? = null
    private var pathList: List<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras

        if (bundle != null) {
            fileModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("fileModel", FileModel::class.java)
            } else {
                intent.getParcelableExtra("fileModel")
            }
        }
        if (bundle != null) {
            pathList = bundle.getStringArrayList("pathList")?.toList()
        }

        setContent {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

            FileManagerTheme {
                Scaffold(topBar = { AppTopBar(scrollBehavior) },
                    modifier = Modifier.fillMaxSize(),
                    content = { paddingValues ->
                        DeletedFileListBody(paddingValues = paddingValues)

                    })
            }
        }
    }

    @Composable
    private fun DeletedFileListBody(paddingValues: PaddingValues) {
        val viewModel: DeletedIFilesViewModel = viewModel(
            factory = AppViewModelProvider.Factory
        )

        val deletedFileUiState by viewModel.deletedFilesListUiState.collectAsState()
        val deletedFileList = deletedFileUiState.deletedFilesList


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            if (deletedFileList.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.trash_is_empty),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

            } else {
                DeletedFileList(
                    innerPadding = paddingValues, deletedFileList = deletedFileList
                )
            }

            if (!pathList.isNullOrEmpty()) {
                SaveDeletedFilesToDatabase(pathList = pathList!!)
            }

        }
    }


    @Composable
    fun DeletedFileList(innerPadding: PaddingValues, deletedFileList: List<DeletedFile>) {
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),

            ) {
            items(items = deletedFileList, key = { it.id }) { file ->
                FileItem(
                    file = file
                )
            }

        }
    }


    @Composable
    fun FileItem(file: DeletedFile) {
        val image: Painter = painterResource(id = R.drawable.ic_folder)
        val resolvedColor = MaterialTheme.colorScheme.onSecondary
        val colorOnSecondary = Color(resolvedColor.toArgb())
        var expanded by remember { mutableStateOf(false) }
        var deleted by remember { mutableStateOf(false) }
        var showBottomSheetInfo by remember { mutableStateOf(false) }

        val extraHeight by animateDpAsState(
            targetValue = if (expanded) 100.dp else 75.dp, animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
            ), label = ""
        )

        AnimatedVisibility(
            visible = !deleted, enter = expandHorizontally(), exit = shrinkHorizontally()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(extraHeight)
                    .padding(start = 8.dp, end = 8.dp)

            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(colorOnSecondary, shape = Shapes().medium)
                    .clickable { expanded = !expanded }
                    .padding(8.dp)) {

                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary, shape = Shapes().medium
                            )
                            .size(width = 55.dp, height = 55.dp)
                    ) {
                        Image(
                            painter = image,
                            contentDescription = null,
                            modifier = Modifier
                                .size(width = 30.dp, height = 30.dp)
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary)
                        )

                    }
                    Column(
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                    ) {
                        Text(text = file.fileName)
                        AnimatedVisibility(visible = expanded) {
                            Text(
                                text = file.filePath,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

            }
        }
        AnimatedVisibility(
            visible = expanded && !deleted,
        ) {
            DeletedFileOptionsRow(modifier = Modifier.background(
                colorOnSecondary, shape = Shapes().medium
            ), file = file, isDeleted = { isDeleted -> deleted = isDeleted })
        }

    }

    @Composable
    fun SaveDeletedFilesToDatabase(pathList: List<String>) {

        pathList.forEach { path ->
            val file = File(path)
            SaveDeletedFileToDatabase(file = file)
        }
    }


    @Composable
    fun SaveDeletedFileToDatabase(file: File) {
        val viewModel: DeletedFileEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)

        //2 = 2MB
        val recommendedSize = file.length() < 2 * 1024 * 1024
        if (recommendedSize) {
            val deletedFileDetails = file.toDeletedFileDetails()

            viewModel.updateUiState(deletedFileDetails)

            LaunchedEffect(viewModel) {
                viewModel.saveDeletedFile(deletedFileDetails = deletedFileDetails)
            }
        }
    }
    /*
        @Composable
        fun SaveDeletedFileToDatabase(file: File) {
            val viewModel: DeletedFileEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)

            //2 = 2MB
            val recommendedSize = file.length() < 2 * 1024 * 1024
            if (recommendedSize) {
                val updatedDetails = viewModel.deletedFileUIState.deletedFileDetails.copy(
                    fileName = file.name, filePath = file.path, fileData = file.readBytes()
                )

                viewModel.updateUiState(updatedDetails)

                LaunchedEffect(viewModel) {
                    viewModel.saveDeletedFile()
                }
            }
        }
    */

    @Composable
    fun RemoveDeletedFileToDatabase(file: DeletedFile) {
        val viewModel: DeletedFileDetailsViewModel =
            viewModel(factory = AppViewModelProvider.Factory)
        val coroutineScope = rememberCoroutineScope()


        LaunchedEffect(viewModel) {
            coroutineScope.launch {
                viewModel.deleteFileFromDatabase(file)
            }

        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppTopBar(scrollBehavior: TopAppBarScrollBehavior) {
        LargeTopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.experimental_trash_can),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            scrollBehavior = scrollBehavior
        )
    }


    @Composable
    fun DeletedFileOptionsRow(
        modifier: Modifier = Modifier,
        file: DeletedFile,
        isDeleted: (Boolean) -> Unit,
        viewModel: DeletedFileDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
    ) {

        val coroutineScope = rememberCoroutineScope()
        var deleted by remember { mutableStateOf(false) }
        val showBottomSheet = remember { mutableStateOf(false) }


        LaunchedEffect(deleted) {
            if (deleted) {
                isDeleted(true)
                viewModel.viewModelScope.launch {
                    delay(3000L)
                    viewModel.deleteFileFromDatabase(deletedFile = file)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(Shapes.large),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,

            ) {

            DeletedFileOption(
                title = stringResource(id = R.string.restore), modifier = modifier.weight(1f)
            ) {

                coroutineScope.launch() {
                    val result = viewModel.restoreDeletedFile(deletedFile = file)
                    launch(Dispatchers.Main) {
                        deleted = result
                    }
                }
            }
            DeletedFileOption(
                title = stringResource(id = R.string.delete), modifier = modifier.weight(1f)
            ) {
                // deleted = true
                showBottomSheet.value = true
            }

            if (showBottomSheet.value) {
                BottomSheetInfo()
            }

        }
    }


    @Composable
    fun DeletedFileOption(
        title: String, modifier: Modifier = Modifier,
        onClick: () -> Unit,
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Row(modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
                .clickable { onClick() }
                .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()

                )

            }

        }
    }
}

