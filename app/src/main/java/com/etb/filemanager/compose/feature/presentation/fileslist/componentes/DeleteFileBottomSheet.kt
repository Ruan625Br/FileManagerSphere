/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - DeleteFileBottomSheet.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.fileslist.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RestoreFromTrash
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etb.filemanager.R
import com.etb.filemanager.ui.theme.Shapes
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteFileBottomSheet(
    pathList: List<String>,
    onDeleteClick: () ->  Unit
) {

    val title = if (pathList.size > 1) "${pathList.size} files" else File(pathList.first()).name


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
            FileNameContainer(pathList = pathList) {

            }

        }
        Spacer(modifier = Modifier.size(16.dp))
        FileOptions(
            onClick = { onDeleteClick() }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

}


@Composable
private fun FileNameContainer(
    pathList: List<String>,
    content: @Composable () -> Unit
) {

    val title = if (pathList.size > 1) "${pathList.size} files" else File(pathList.first()).name

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.extraLarge)
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
                        text = title,
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
fun FileOptions(
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.large)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.delete),
                )
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.RestoreFromTrash,
                    contentDescription = stringResource(R.string.delete)
                )
            },
            modifier = Modifier
                .clickable { onClick() }
                .clip(RoundedCornerShape(100)),
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

    }
}
