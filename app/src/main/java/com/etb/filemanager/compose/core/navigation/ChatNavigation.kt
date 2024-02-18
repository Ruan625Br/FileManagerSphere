/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatNavigation.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.etb.filemanager.compose.feature.presentation.chat_screen.ChatScreen
import com.etb.filemanager.compose.feature.presentation.chat_screen.ChatViewModel

const val ChatRoute = "chat"

fun NavGraphBuilder.chatScreen(
    paddingValues: PaddingValues
) {
    composable(
        route = ChatRoute
    ) {
        val viewModel: ChatViewModel = hiltViewModel()
        val uiState by viewModel.state.collectAsStateWithLifecycle()

        ChatScreen(uiState = uiState,
            paddingValues = paddingValues,
            onClickSendMsg = { msg ->
                viewModel.sendMessage(msg)
            },
            onClickChat = { viewModel.setCurrentChat(it) }) { viewModel.newChat() }
    }
}

fun NavController.navigateToChat(navOptions: NavOptions? = null) {
    navigate(ChatRoute, navOptions)
}
