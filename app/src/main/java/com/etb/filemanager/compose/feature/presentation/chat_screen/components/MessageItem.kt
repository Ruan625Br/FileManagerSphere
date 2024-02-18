/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - MessageItem.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.chat_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etb.filemanager.R
import com.etb.filemanager.compose.core.models.Message
import com.etb.filemanager.compose.core.models.Participant
import com.etb.filemanager.compose.core.presentation.components.LoadingAnimation
import com.etb.filemanager.compose.core.presentation.util.filterMessage

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    message: Message
) {
    val filteredMessage = message.text.filterMessage()
    val participant = message.participant
    val isUser = participant == Participant.USER || participant == Participant.USER_ERROR
    val shape = RoundedCornerShape(
        topStart = if (isUser || participant == Participant.ERROR) 16.dp else 0.dp,
        topEnd = if (!isUser) 16.dp else 0.dp,
        bottomStart = 16.dp, bottomEnd = 16.dp
    )
    val horizontalAlignment = when (participant) {
        Participant.MODEL -> Alignment.Start
        Participant.ERROR -> Alignment.CenterHorizontally
        else -> Alignment.End

    }
    val bgColor = when {
        isUser -> MaterialTheme.colorScheme.primaryContainer
        message.isPending -> MaterialTheme.colorScheme.tertiaryContainer
        participant == Participant.MODEL -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }

    var isListOperationVisible by remember {
        mutableStateOf(false)
    }

    var isOriginalMessage by remember {
        mutableStateOf(false)
    }

    var isExpandedDropdownMenu by remember {
        mutableStateOf(false)
    }
    val msgText = if (isOriginalMessage) message.text else filteredMessage

    Column(
        modifier = Modifier
            .fillMaxWidth()
        ,
        horizontalAlignment = horizontalAlignment
    ) {
        Column(
            modifier = modifier
                .padding(start = 10.dp, end = 10.dp)
                .background(color = bgColor, shape = shape),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (message.isPending) {
                    LoadingAnimation(
                        modifier = Modifier
                            .padding(8.dp),
                        circleSize = 8.dp,
                        circleColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(5.dp),
                    text = msgText,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        AnimatedVisibility(visible = isListOperationVisible) {
            ListOperationResults(
                modifier = Modifier
                   .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                results = message.operationResults)
        }

        if (participant == Participant.USER_ERROR){
            ErrorMessage(
                modifier = Modifier
                    .padding(end = 10.dp),
            )
        }

        if (participant == Participant.MODEL) {
            Box(modifier = Modifier
                .align(Alignment.End)){
                IconButton(modifier = Modifier, onClick = { isExpandedDropdownMenu = !isExpandedDropdownMenu }) {
                    Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
                }
                MessageDropDownMenu(
                    expanded = isExpandedDropdownMenu,
                    onDismissRequest = { isExpandedDropdownMenu = !isExpandedDropdownMenu},
                    onClickShowOperationsResults = { isListOperationVisible = !isListOperationVisible },
                    onClickShowOriginalMessage = { isOriginalMessage = !isOriginalMessage })
            }
        }

    }

}

@Composable
private fun ErrorMessage(
    modifier: Modifier = Modifier,
    error: String = stringResource(id = R.string.message_not_sent)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            modifier = Modifier
                .size(16.dp),
            imageVector = Icons.Rounded.ErrorOutline,
            tint = MaterialTheme.colorScheme.error,
            contentDescription = null
        )
        Text(
            text = error,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.error
        )
    }
}