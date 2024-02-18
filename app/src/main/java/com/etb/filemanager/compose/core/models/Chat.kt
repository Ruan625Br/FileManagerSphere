/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatUtils.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.models

data class Chat(
    val id: Int = 0,
    val chatSettings: ChatSettings = ChatSettings(),
    val messages: List<Message> = emptyList(),
)
