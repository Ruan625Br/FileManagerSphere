/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatTextField.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.chat_screen.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.etb.filemanager.compose.core.models.FileOperationItem
import com.etb.filemanager.files.provider.archive.common.mime.MimeType
import kotlin.io.path.Path

@Composable
fun ChatTextField(
    modifier: Modifier = Modifier,
    onClickSendMsg: (String) -> (Unit),
) {
    var value by rememberSaveable {
        mutableStateOf("")
    }

    val operations = listOf(
        FileOperationItem("Rename File", "I renamed the file MyFile01 in the Download folder to MyFile-01"),
        FileOperationItem("Create File", "Create a file named MyFile01.txt in the Download folder"),
        FileOperationItem("Write to File", "Write a list of mathematics exercises to the file MyFile01.txt in the Download folder"),
        FileOperationItem("Delete File", "Delete the file MyFile01.txt from the Download folder"))

    var fileUri by remember {
        mutableStateOf<Uri?>(null)
    }

    Column {

        ListFileOperations(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            operations = operations, onClickOperation = {
            value = it.content
        })

        AnimatedVisibility(visible = fileUri != null) {
            val path = Path(fileUri?.path!!)
            Text(text = path.fileName.toString())
        }

        OutlinedTextField(modifier = modifier.fillMaxWidth(),
            value = value,
            onValueChange = { value = it },
            label = {
                Text(text = "Message")
            },
            leadingIcon = {
                 AnimatedVisibility(visible = fileUri == null) {
                     ButtonPickerFiles(onFilePicker = {
                         fileUri = it
                     })
                 }
            },
            trailingIcon = {
                AnimatedVisibility(visible = value.isNotBlank()) {
                    IconButton(onClick = {
                        onClickSendMsg(value)
                        value = ""
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun ButtonPickerFiles(
    onFilePicker: (Uri) -> Unit
) {
    var result by remember {
        mutableStateOf<Uri?>(null)
    }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument(), onResult = {
        result = it
    })


    IconButton(onClick = {
        launcher.launch(arrayOf(MimeType.DIRECTORY.value, MimeType.IMAGE_ANY.value, MimeType.PDF.value, MimeType.TEXT_PLAIN.value))
        result?.let { onFilePicker(it) }
    }) {
        Icon(imageVector = Icons.Rounded.UploadFile, contentDescription = null)
    }
}