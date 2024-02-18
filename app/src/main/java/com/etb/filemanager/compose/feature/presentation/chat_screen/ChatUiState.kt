/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatUiState.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.chat_screen

import com.etb.filemanager.compose.core.models.Chat

data class ChatUiState(
    val chatList: List<Chat> = emptyList(),
    val chat: Chat = Chat(),
)