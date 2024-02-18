/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - CategoryListNavigation.kt
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
import com.etb.filemanager.compose.feature.presentation.categorylist.components.CategoryListScreen
import com.etb.filemanager.manager.category.adapter.CategoryFileModel

const val CategoryListRoute = "category_list"

fun NavGraphBuilder.categoryListScreen(
    paddingValues: PaddingValues,
    categoryFileModel: CategoryFileModel?,
    onNavigateToMediaView: (Bundle) -> Unit
){
    composable(CategoryListRoute){

        CategoryListScreen(
            innerPadding = paddingValues,
            categoryFileModel = categoryFileModel,
            navigate = {_, args ->
                onNavigateToMediaView(args)
            })
    }
}

fun NavController.navigateToCategoryList(navOptions: NavOptions? = null){
    navigate(CategoryListRoute, navOptions)
}