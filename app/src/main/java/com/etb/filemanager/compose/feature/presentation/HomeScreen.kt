/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - HomeScreen.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.etb.filemanager.compose.core.navigation.ChatRoute
import com.etb.filemanager.compose.core.navigation.NavigationComp
import com.etb.filemanager.compose.feature.provider.BaseScreen
import com.etb.filemanager.files.extensions.parcelable
import com.etb.filemanager.manager.category.adapter.CategoryFileModel
import com.etb.filemanager.manager.files.services.ScreenshotObserverService
import com.etb.filemanager.ui.theme.FileManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreen : BaseScreen() {
    private var categoryFileModel: CategoryFileModel? = null
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        val startDestination = intent.getStringExtra("startDestination")!!

        when {
            startDestination != ChatRoute && bundle != null -> {
                categoryFileModel = bundle.parcelable("categoryFileModel")

            }
            startDestination == ChatRoute -> {
                imagePath = intent.getStringExtra(ScreenshotObserverService.SCREENSHOT_PATH)
            }

        }

        setContent {
            val navController = rememberNavController()

            FileManagerTheme {
                Scaffold(
                    content = { innerPadding ->
                        NavigationComp(
                            navController = navController,
                            startDestination = startDestination,
                            paddingValues = innerPadding,
                            categoryFileModel = categoryFileModel,
                            imagePath = imagePath
                        )
                    }
                )

            }
        }
    }
}


