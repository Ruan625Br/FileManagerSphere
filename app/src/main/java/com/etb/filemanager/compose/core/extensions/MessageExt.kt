/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - MessageExt.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.extensions

import com.etb.filemanager.compose.core.models.Message
import com.etb.filemanager.compose.core.models.Participant
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content

fun List<Message>.hasPendingMessage(): Boolean = any { it.isPending }

fun List<Message>.toContent(): List<Content> {
    val filteredList = filter { !it.isPending && it.participant != Participant.ERROR && it.participant != Participant.USER_ERROR }

    return filteredList.map { msg ->
        val role = if (msg.participant == Participant.USER) "user" else "model"
        content(role = role) {
            text(msg.text)
        }
    }
}
