/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - APKInfoRow.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.files.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.ui.theme.FileManagerTheme
import com.etb.filemanager.ui.theme.Shapes
import java.io.File


//temporarily discontinued
@Composable
fun APKInfoRow(
    modifier: Modifier = Modifier,
    label: String,
    content: String,
    file: FileModel
) {

    ListItem(
        modifier = modifier
            .fillMaxSize()
            .clip(Shapes.medium),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        headlineContent = {
            Text(
                text = file.fileName,
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = {
            Text(text = content)
        }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = false)
@Composable
fun teste() {
    FileManagerTheme {
          val file = File("")
        val fileModel = FileModel(
            0L,
            "FileManagerSphere.apk",
            "path/to/FileManagerSphere.apk",
            false,
            ".apk",
            9L,
            file,
            false
            )
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
                APKNameContainer(
                    file = fileModel) {
                    APKInfoRow(label = "Label aqui", content = "Content", file = fileModel )

                }

            }
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(MaterialTheme.colorScheme.onSurface),
                onClick = { /* Ação a ser executada quando o botão for clicado */ }
            ) {
                Text(text = "Instalar")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun APKNameContainer(
    file: FileModel,
    content: @Composable () -> Unit
){
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
                        text = file.fileName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = file.fileName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        content()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun showInstallAPKBottomSheet(
    fileModel: FileModel
){
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
            APKNameContainer(
                file = fileModel) {
                APKInfoRow(label = "Label aqui", content = "Content", file = fileModel )

            }

        }
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.onSurface),
            onClick = { /* Ação a ser executada quando o botão for clicado */ }
        ) {
            Text(text = "Instalar")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

}