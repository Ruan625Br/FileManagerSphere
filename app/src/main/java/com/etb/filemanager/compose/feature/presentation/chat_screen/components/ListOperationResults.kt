/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ListOperationResults.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.chat_screen.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.etb.filemanager.compose.core.models.FileOperationResult
import com.etb.filemanager.ui.theme.Shapes

@Composable
fun ListOperationResults(modifier: Modifier = Modifier, results: List<FileOperationResult>) {
    Card(modifier = modifier
        .height(143.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        )) {
        LazyColumn(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            items(results) { result ->
                OperationResultItem(result = result)
            }
        }
    }
}

@Composable
private fun OperationResultItem(result: FileOperationResult) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    val height by animateDpAsState(targetValue = if (isExpanded) 80.dp else 30.dp, label = "")

    Column(modifier = Modifier
        .padding(horizontal = 8.dp)
        .clickable { isExpanded = !isExpanded }
        .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp), shape = Shapes.medium)
        .height(height)) {
        Row(modifier = Modifier.fillMaxWidth()
            .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = result.name)
            Text(text = "Status: Not found", color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        Text(
            modifier = Modifier.padding(start = 15.dp), text = result.desc
        )
    }
}