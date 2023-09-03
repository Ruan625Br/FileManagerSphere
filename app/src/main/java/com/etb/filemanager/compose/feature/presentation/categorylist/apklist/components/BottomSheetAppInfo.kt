/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - BottomSheetAppInfo.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.categorylist.apklist.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etb.filemanager.files.extensions.AppFilter
import com.etb.filemanager.files.extensions.AppInfo
import com.etb.filemanager.files.extensions.AppMetadata
import com.etb.filemanager.files.extensions.getAppMetadata
import com.etb.filemanager.ui.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetAppInfo(
    modifier: Modifier = Modifier,
    appInfo: AppInfo,
    appFilter: AppFilter,
    painter: Painter,
    metadataList: MutableList<AppMetadata> = getAppMetadata(appInfo, LocalContext.current, appFilter != AppFilter.UNINSTALLED_INTERNAL),
    onDismissRequest: () -> Unit

) {
    ModalBottomSheet(
        modifier = modifier
            .padding(horizontal = 16.dp),
        onDismissRequest = onDismissRequest,
        shape = Shapes.extraSmall,
        //sheetState = bottomSheetState.sheetState,
        containerColor = Color.Transparent,
        contentColor = Color.White,
        dragHandle = null
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AppInfoContainer(appInfo = appInfo, painter = painter) {
                for (metadata in metadataList){
                    AppInfoRow(label = metadata.label, content = metadata.content)
                }

            }
            ApkItemOptionsColum(appFilter = appFilter, apkInfo = appInfo, isAppInfoBottomSheetExpanded = true)
            Spacer(modifier = Modifier.size(16.dp))

        }
    }
}

@Composable
private fun AppInfoContainer(
    appInfo: AppInfo,
    painter: Painter,
    content: @Composable () -> Unit
) {
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
            Image(
                painter = painter, contentDescription = null, modifier = Modifier.size(50.dp)
            )
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
                        text = appInfo.appName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = appInfo.packageName,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppInfoRow(
    modifier: Modifier = Modifier,
    label: String,
    content: String,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clip(Shapes.medium)
            .combinedClickable(
                onClick = { onClick?.let { it() } },
                onLongClick = {
                    if (onLongClick != null) onLongClick()
                    else {
                        clipboardManager.setText(AnnotatedString(content))
                    }
                }
            ),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        headlineContent = {
            Text(
                text = label,
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = {
            Text(text = content)
        },
    )
}