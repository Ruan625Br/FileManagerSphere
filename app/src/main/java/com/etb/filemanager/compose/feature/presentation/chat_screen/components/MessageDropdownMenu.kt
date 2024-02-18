/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - MessageDropdownMenu.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.chat_screen.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun MessageDropDownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onClickShowOperationsResults: () -> Unit,
    onClickShowOriginalMessage: () -> Unit
) {
    var isOriginalMessage by remember { mutableStateOf(false) }

    val buttonText = if (isOriginalMessage) "Show filtered message" else "Show original message"

    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        DropdownMenuItem(
            text = { Text("Show results of operations") },
            onClick = onClickShowOperationsResults
        )

        DropdownMenuItem(
            text = { Text(buttonText) },
            onClick = {
                isOriginalMessage = !isOriginalMessage
                onClickShowOriginalMessage()
            }
        )
    }
}
