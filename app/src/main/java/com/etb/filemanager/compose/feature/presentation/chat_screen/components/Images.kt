/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - Images.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.chat_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.etb.filemanager.ui.theme.Shapes

@Composable
fun Images(
    bitmapList: List<ImageBitmap>,
    modifier: Modifier = Modifier
) {
    var imagesVisible  by rememberSaveable {
        mutableStateOf(bitmapList.isNotEmpty())
    }
    val icon = if (imagesVisible) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowUp

    Column {
        if (bitmapList.isNotEmpty()) {
            IconButton(
                onClick = { imagesVisible = !imagesVisible }) {
                Icon(imageVector = icon, contentDescription = null)
            }
        }
        AnimatedVisibility(
            visible = imagesVisible) {
            LazyRow(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(bitmapList) { imageBitmap ->
                    ImageItem(imageBitmap = imageBitmap)
                }
            }
        }
    }
}

@Composable
private fun ImageItem(imageBitmap: ImageBitmap) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clip(Shapes.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        ),
        shape = Shapes.large) {
        Box(modifier = Modifier
            .clip(Shapes.large)
            .fillMaxSize()
            .padding(8.dp)) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(Shapes.medium),
                bitmap = imageBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop)
        }
    }
}