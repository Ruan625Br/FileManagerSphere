/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatScreen.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.chat_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.etb.filemanager.compose.core.extensions.hasPendingMessage
import com.etb.filemanager.compose.core.models.Chat
import com.etb.filemanager.compose.core.models.ChatSettings
import com.etb.filemanager.compose.core.models.Message
import com.etb.filemanager.compose.feature.presentation.chat_screen.components.ChatNavigationDrawer
import com.etb.filemanager.compose.feature.presentation.chat_screen.components.ChatTextField
import com.etb.filemanager.compose.feature.presentation.chat_screen.components.CustomTopAppBar
import com.etb.filemanager.compose.feature.presentation.chat_screen.components.MessageList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    uiState: ChatUiState = ChatUiState(),
    paddingValues: PaddingValues,
    onClickSendMsg: (String) -> Unit,
    onClickChat: (Chat) -> Unit,
    onClickNewChat: () -> Unit,
) {

    val chatList = uiState.chatList
    val currentChat = uiState.chat
    val chatSettings = uiState.chat.chatSettings
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ChatNavigationDrawer(
        drawerState = drawerState,
        chatList = chatList,
        currentChat = currentChat,
        onClickChat = onClickChat,
        onClickNewChat = onClickNewChat
    ) {

        ChatScreenContent(
            coroutineScope = coroutineScope,
            paddingValues = paddingValues,
            messages = uiState.chat.messages,
            chatSettings = chatSettings,
            onClickSendMsg = { msg ->
                onClickSendMsg(msg)
            }
        ) {
            coroutineScope.launch {
                if (drawerState.isClosed) drawerState.open() else drawerState.close()
            }
        }
    }
}

@Composable
fun ChatScreenContent(
    coroutineScope: CoroutineScope,
    paddingValues: PaddingValues,
    messages: List<Message>,
    chatSettings: ChatSettings,
    onClickSendMsg: (String) -> Unit,
    onClickOpenDrawer: () -> Unit,
) {
    val listState = rememberLazyListState()

    Column {

        CustomTopAppBar(chatSettings = chatSettings, onClickOpenDrawer = onClickOpenDrawer)

        MessageList(
            modifier = Modifier
                .padding(top = 10.dp)
                .weight(1f),
            state = listState,
            list = messages,
            paddingValues = paddingValues

        )

        ChatTextField(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
        ) { msg ->
            if (!messages.hasPendingMessage()) {
                onClickSendMsg(msg)
                coroutineScope.launch {
                    if (messages.isNotEmpty()) {
                        listState.animateScrollToItem(messages.size - 1, 0)
                    }
                }
            }
        }
    }
}