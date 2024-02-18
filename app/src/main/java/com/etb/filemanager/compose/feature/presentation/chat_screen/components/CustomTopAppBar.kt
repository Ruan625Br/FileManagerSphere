/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - CustomTopAppBar.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.chat_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.etb.filemanager.compose.core.models.ChatSettings

@Composable
fun CustomTopAppBar(
    modifier: Modifier = Modifier, chatSettings: ChatSettings, onClickOpenDrawer: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
    ) {

        val (btnOpenDrawer, textChatTitle, textSubtitle) = createRefs()

        IconButton(modifier = Modifier
            .size(56.dp)
            .constrainAs(btnOpenDrawer) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
            }, onClick = onClickOpenDrawer) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.MenuOpen, contentDescription = null)
        }

        Text(modifier = Modifier.constrainAs(textChatTitle) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
        }, text = chatSettings.title, fontSize = 22.sp)

        Text(modifier = Modifier.constrainAs(textSubtitle) {
            top.linkTo(textChatTitle.bottom)
            start.linkTo(textChatTitle.start)
            end.linkTo(textChatTitle.end)
        }, text = "Experimental", fontSize = 14.sp)
    }
}