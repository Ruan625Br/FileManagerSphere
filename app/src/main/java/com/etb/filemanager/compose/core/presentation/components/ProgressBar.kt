/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ProgressBar.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etb.filemanager.ui.theme.Shapes

@Composable
fun ProgressBar(
    progress: Float,
    maxProgress: Float = 100.0f
) {
    var mProgress by remember { mutableFloatStateOf(0f) }
    val targetSize = (mProgress / maxProgress).coerceIn(0f, 1f)

    val size by animateFloatAsState(
        targetValue = targetSize, tween(
            durationMillis = 1000, delayMillis = 500, easing = LinearOutSlowInEasing
        ), label = "Progress Size"
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(Shapes.large)
                    .background(MaterialTheme.colorScheme.inversePrimary)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(size)
                    .fillMaxHeight()
                    .clip(Shapes.large)
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .animateContentSize()
            )


        }
    }

    LaunchedEffect(progress){
        mProgress = progress
    }
}

@Composable
fun CircularProgressBar(
    progress: Float,
    text: String = "",
    subText: String = "",
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 28.sp,
    subFontSize: TextUnit = 21.sp,
    strokeWidth: Dp = 8.dp,
    animaDuration: Int = 1000,
    animDelay: Int = 500,
    textColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    trackColor: Color = MaterialTheme.colorScheme.inversePrimary,
    indicatorColor: Color = MaterialTheme.colorScheme.onPrimary,
    maxProgress: Float = 100.0f

) {
    var mProgress by remember { mutableFloatStateOf(0f) }
    val targetSize = (mProgress / maxProgress).coerceIn(0f, 1f)

    val progressValue by animateFloatAsState(
        targetValue = targetSize, tween(
            durationMillis = animaDuration, delayMillis = animDelay, easing = LinearOutSlowInEasing
        ), label = "Progress Size"
    )

   LaunchedEffect(progress){
       mProgress = progress
   }

   Box(
       contentAlignment = Alignment.Center,
   ) {
       Canvas(
           modifier = modifier
               .size(85.dp)){
           drawArc(
               color = trackColor,
               -90f,
               360 * maxProgress,
               useCenter = false,
               style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
           )
           drawArc(
               color = indicatorColor,
               -90f,
               360 * progressValue,
               useCenter = false,
               style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
           )

       }
       Column(
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.Center,
           modifier = Modifier.size(85.dp)
               .padding(top = 15.dp)
       ) {
           Text(
               text = text,
               color = textColor,
               fontSize = fontSize)
           Text(
               text = subText,
               color = textColor,
               fontSize = subFontSize)
       }

   }
}
