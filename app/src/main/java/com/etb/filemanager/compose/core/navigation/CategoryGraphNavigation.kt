/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - CategoryGraphNavigation.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import com.etb.filemanager.manager.category.adapter.CategoryFileModel

const val CategoryGraphPattern = "category"

fun NavController.navigateToCategoryGraph(navOptions: NavOptions? = null) {
    navigate(CategoryGraphPattern, navOptions)
}

fun NavGraphBuilder.categoryGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    categoryFileModel: CategoryFileModel?
) {
    navigation(
        startDestination = CategoryListRoute, route = CategoryGraphPattern
    ) {
        categoryListScreen(
            paddingValues = paddingValues,
            categoryFileModel = categoryFileModel,
            onNavigateToMediaView = {
                navController.navigateToMediaView(it)
            }
        )

        mediaViewScreen(
            paddingValues = paddingValues
        )
    }
}