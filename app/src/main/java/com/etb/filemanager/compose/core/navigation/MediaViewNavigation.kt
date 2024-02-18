/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - MediaViewNavigation.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.navigation

import android.os.Bundle
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.etb.filemanager.files.extensions.navigate
import com.etb.filemanager.files.extensions.parcelable
import com.etb.filemanager.manager.media.MediaViewScreen
import com.etb.filemanager.manager.media.model.MediaListInfo

const val MediaViewRoute = "media_view"


fun NavGraphBuilder.mediaViewScreen(
    paddingValues: PaddingValues
){

    composable(
        route = MediaViewRoute,
    ){

        val mediaListInfo = it.arguments?.parcelable<MediaListInfo>("mediaInfo")
        MediaViewScreen(
            mediaListInfo = mediaListInfo!!,
            paddingValues = paddingValues,
            toggleRotate = { /*TODO*/ }) {

        }
    }
}

fun NavController.navigateToMediaView(arg: Bundle, navOptions: NavOptions? = null){
    navigate(MediaViewRoute, arg, navOptions)
}