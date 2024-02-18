/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatNavigationDrawer.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.chat_screen.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Message
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.etb.filemanager.compose.core.models.Chat
import com.etb.filemanager.ui.theme.Shapes


@Composable
fun ChatNavigationDrawer(
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    chatList: List<Chat>,
    currentChat: Chat,
    onClickChat: (Chat) -> Unit,
    onClickNewChat: () -> Unit,
    content: @Composable () -> Unit
) {

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {

                    ButtonNewChat(
                        onClick = onClickNewChat
                    )
                    DrawerContent(
                        chats = chatList,
                        chatSelected = currentChat,
                        onClickChat =  onClickChat)
                }
            }
        }) {
        content()
    }
}


@Composable
private fun DrawerContent(
    chats: List<Chat>, chatSelected: Chat?, onClickChat: (Chat) -> Unit
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(chats, key = { it.id}) { chat ->
            DrawerItem(chat = chat,
                selected = chatSelected == chat,
                onClick = { onClickChat(chat) })
        }
    }
}

@Composable
private fun DrawerItem(
    chat: Chat, selected: Boolean, onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Message,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        label = {
            Text(text = chat.chatSettings.title)
        },
        shape = Shapes.large,
        selected = selected, onClick = onClick)
}


@Composable
private fun ButtonNewChat(
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth(),
        shape = Shapes.large,
        onClick = onClick) {
        Text(text = "New chat")
    }
}