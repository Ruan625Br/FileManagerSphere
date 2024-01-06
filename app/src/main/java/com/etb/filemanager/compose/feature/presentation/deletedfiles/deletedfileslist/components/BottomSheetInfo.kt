/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - BottomSheetInfo.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.deletedfiles.deletedfileslist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etb.filemanager.R
import com.etb.filemanager.ui.theme.AppShapes
import com.etb.filemanager.ui.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetInfo() {
    ModalBottomSheet(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        onDismissRequest = {
        },
        shape = Shapes.extraSmall,
        containerColor = Color.Transparent,
        contentColor = Color.White,
        dragHandle = null
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            BottomSheetContainer {

            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        BottomSheetButtonsRow()
        Spacer(modifier = Modifier.size(16.dp))

    }
}



@Composable
private fun BottomSheetContainer(
    content: @Composable () -> Unit
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = "FileManagerSphere",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        content()
    }
}

@Composable
fun BottomSheetButtonsRow(){
    val resolvedColor = MaterialTheme.colorScheme.onSecondary
    val colorOnSecondary = Color(resolvedColor.toArgb())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(Shapes.large),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        BottomSheetButton(
            text = stringResource(id = R.string.ok),
            modifier = Modifier
                .weight(1f)
                .background(color = colorOnSecondary, shape = AppShapes.cutLeftMedium)) {
        }
        BottomSheetButton(
            text = stringResource(id = R.string.cancel),
            modifier = Modifier
                .weight(1f)
                .background(color = colorOnSecondary, shape = AppShapes.cutRightMedium)) {
        }
    }
}

@Composable
fun BottomSheetButton(
     text: String,
     modifier: Modifier = Modifier,
     onClick: () -> Unit
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Row(modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { onClick() }
            .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth()

            )

        }

    }

}



