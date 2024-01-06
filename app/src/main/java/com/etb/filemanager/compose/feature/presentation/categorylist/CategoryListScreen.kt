/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - CategoryListScreen.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.categorylist

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.rememberNavController
import com.etb.filemanager.compose.core.presentation.components.NavigationComp
import com.etb.filemanager.compose.feature.provider.BaseScreen
import com.etb.filemanager.files.extensions.parcelable
import com.etb.filemanager.files.util.toggleOrientation
import com.etb.filemanager.manager.category.adapter.CategoryFileModel
import com.etb.filemanager.manager.category.adapter.getName
import com.etb.filemanager.ui.theme.FileManagerTheme

class CategoryListScreen : BaseScreen() {
    private var categoryFileModel: CategoryFileModel? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        if (bundle != null) {
            categoryFileModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("categoryFileModel", CategoryFileModel::class.java)
            } else {
                intent.parcelable("categoryFileModel")
            }
        }

        setContent {
            val navController = rememberNavController()

            FileManagerTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = categoryFileModel?.category.getName(LocalContext.current),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                        )
                    },
                    content = { innerPadding ->
                       NavigationComp(
                           navController = navController,
                           categoryFileModel = categoryFileModel!!,
                           paddingValues = innerPadding,
                           toggleRotate = ::toggleOrientation)
                    }
                )

            }
        }
    }
}


