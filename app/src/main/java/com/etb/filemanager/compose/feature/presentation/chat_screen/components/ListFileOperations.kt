/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ListFileOperations.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.chat_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.etb.filemanager.compose.core.models.FileOperationItem
import com.etb.filemanager.ui.theme.Shapes

@Composable
fun ListFileOperations(
    modifier: Modifier = Modifier,
    operations: List<FileOperationItem>,
    onClickOperation: (FileOperationItem) -> Unit,
) {
    var listVisible by rememberSaveable {
        mutableStateOf(true)
    }

    val icon = if (listVisible) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowUp

    Column {
        IconButton(
            onClick = { listVisible = !listVisible}) {
            Icon(imageVector = icon, contentDescription = null)
        }
        AnimatedVisibility(
            visible = listVisible) {
            LazyRow(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(operations) { operation ->
                    FileOperationItem(
                        modifier = Modifier
                            .clickable {
                                onClickOperation(operation)
                            }, operation = operation
                    )
                }
            }
        }
    }
}

@Composable
private fun FileOperationItem(
    modifier: Modifier = Modifier, operation: FileOperationItem
) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clip(Shapes.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        ),
        shape = Shapes.large) {
        Box(modifier = modifier
            .clip(Shapes.large)
            .fillMaxSize()
            .padding(8.dp)) {
            Text(
                modifier = Modifier.align(Alignment.TopStart), text = operation.title)
            Icon(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                imageVector = Icons.Rounded.Draw, contentDescription = null
            )
        }
    }
}