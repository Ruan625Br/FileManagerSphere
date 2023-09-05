/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - Screen.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.presentation.util

sealed class Screen(val route: String){
    object CategoryListScreen : Screen("category_list_screen")
    object MediaViewScreen : Screen("media_screen")

    operator fun invoke() = route
}
