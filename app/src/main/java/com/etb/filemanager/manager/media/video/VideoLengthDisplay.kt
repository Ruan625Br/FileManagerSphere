/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - VideoLengthDisplay.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.media.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.etb.filemanager.manager.media.model.Media
import com.etb.filemanager.ui.util.advancedShadow

@Composable
fun VideoLengthDisplay(modifier: Modifier = Modifier, media: Media) {
    Row(
        modifier = modifier
            .padding(all = 8.dp)
            .advancedShadow(
                cornersRadius = 8.dp,
                shadowBlurRadius = 6.dp,
                alpha = 0.3f
            ),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            modifier = Modifier,
            text = "Teste",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
        Spacer(modifier = Modifier.size(2.dp))
        Image(
            modifier = Modifier
            .size(16.dp)
            .advancedShadow(
                cornersRadius = 2.dp,
                alpha = 0.1f,
                offsetX = (-2).dp
            ),
            imageVector = Icons.Rounded.PlayArrow,
            colorFilter = ColorFilter.tint(color = Color.White),
            contentDescription = "Video")
    }
}