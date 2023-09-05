/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - NavigationComp.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.presentation.components

import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.etb.filemanager.compose.core.presentation.common.ChanneledViewModel
import com.etb.filemanager.compose.core.presentation.util.Screen
import com.etb.filemanager.compose.feature.presentation.categorylist.components.CategoryListScreen
import com.etb.filemanager.manager.category.adapter.CategoryFileModel
import com.etb.filemanager.manager.media.MediaViewScreen
import com.etb.filemanager.manager.media.model.MediaListInfo
import com.etb.filemanager.ui.util.Constants.Animation.navigateInAnimation
import com.etb.filemanager.ui.util.Constants.Animation.navigateUpAnimation

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationComp(
    navController: NavHostController,
    categoryFileModel: CategoryFileModel,
    paddingValues: PaddingValues,
    toggleRotate: () -> Unit,

    ) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val navPipe = hiltViewModel<ChanneledViewModel>()
    navPipe
        .initWithNav(navController)
        .collectAsState(LocalLifecycleOwner.current)

    NavHost(
        navController = navController,
        startDestination = Screen.CategoryListScreen.route
    ) {
        composable(
            route = Screen.CategoryListScreen.route,
            enterTransition = { navigateInAnimation },
            exitTransition = { navigateUpAnimation },
            popEnterTransition = { navigateInAnimation },
            popExitTransition = { navigateUpAnimation }
        ) {
            CategoryListScreen(
                innerPadding = paddingValues,
                categoryFileModel = categoryFileModel,
                navigate = navPipe::navigate
            )
        }
        composable(
            route = Screen.MediaViewScreen.route,
            enterTransition = { navigateInAnimation },
            exitTransition = { navigateUpAnimation },
            popEnterTransition = { navigateInAnimation },
            popExitTransition = { navigateUpAnimation },
        ) { backStackEntry ->
            val mediaInfo =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    backStackEntry.arguments?.getParcelable("mediaInfo", MediaListInfo::class.java)
                } else {
                    backStackEntry.arguments?.getParcelable("mediaInfo")
                }

            MediaViewScreen(
                mediaListInfo = mediaInfo!!,
                paddingValues = paddingValues,
                toggleRotate = toggleRotate,
                navigateUp = navPipe::navigateUp)
        }
    }
}