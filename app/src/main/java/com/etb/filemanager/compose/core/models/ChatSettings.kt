/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatSettings.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatSettings(
    val title: String = "ChatSphere"
)