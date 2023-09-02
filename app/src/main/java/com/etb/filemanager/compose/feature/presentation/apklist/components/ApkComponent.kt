/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ApkComponent.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.apklist.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.etb.filemanager.files.extensions.AppFilter
import com.etb.filemanager.files.extensions.AppInfo
import com.etb.filemanager.files.extensions.installApk
import com.etb.filemanager.files.extensions.openApp
import com.etb.filemanager.files.extensions.openAppSettings
import com.etb.filemanager.files.extensions.uninstallApp
import com.etb.filemanager.ui.theme.AppShapes
import com.etb.filemanager.ui.theme.Shapes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.file.Paths

@Composable
fun ApkItem(apkInfo: AppInfo, appFilter: AppFilter) {
    val resolvedColor = MaterialTheme.colorScheme.onSecondary
    val colorOnSecondary = Color(resolvedColor.toArgb())

    var expanded by remember { mutableStateOf(false) }
    var isAppInfoBottomSheetExpanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    val extraHeight by animateDpAsState(
        targetValue = if (expanded) 100.dp else 70.dp, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        ), label = ""
    )
    val memoryCacheKey = "memory_${apkInfo.packageName}"
    val diskCacheKey = "disk_${apkInfo.packageName}"

    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(apkInfo.appIcon)
        .memoryCacheKey(memoryCacheKey)
        .diskCacheKey(diskCacheKey)
        .build()

    val painter = rememberAsyncImagePainter(
        model = imageRequest,
        contentScale = ContentScale.Crop,
        filterQuality = FilterQuality.None
    )



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(extraHeight)
            .padding(start = 8.dp, end = 8.dp)
            .clip(shape = Shapes().medium)


    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.0.dp))
            .clickable { expanded = !expanded }
            .padding(8.dp)) {


            Image(
                painter = painter, contentDescription = null, modifier = Modifier.size(50.dp)
            )
            Column(


                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = apkInfo.appName
                )

                Text(
                    text = apkInfo.packageName,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

            }
        }

    }

    AnimatedVisibility(
        visible = expanded,
    ) {
        ApkItemOptionsColum(
            appFilter = appFilter,
            apkInfo = apkInfo,
            isAppInfoBottomSheetExpanded = isAppInfoBottomSheetExpanded,
            onButtonClickShowAppInfo = {
                isAppInfoBottomSheetExpanded = true
                coroutineScope.launch {
                    delay(200)
                    expanded = false

                }
            })
    }

    if (isAppInfoBottomSheetExpanded) {
        BottomSheetAppInfo(appInfo = apkInfo, appFilter = appFilter, painter = painter,
            onDismissRequest = {
                isAppInfoBottomSheetExpanded = false
            })

    }

}

@Composable
fun ApkList(
    innerPadding: PaddingValues,
    apkList: List<AppInfo>,
    appFilter: AppFilter = AppFilter.NON_SYSTEM
) {
    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),

        ) {
        items(items = apkList) { apkInfo ->
            ApkItem(apkInfo = apkInfo, appFilter = appFilter)

        }

    }

}

@Composable
fun ApkItemOptionsColum(
    appFilter: AppFilter,
    apkInfo: AppInfo,
    modifier: Modifier = Modifier,
    onButtonClickShowAppInfo: (() -> Unit)? = null,
    isAppInfoBottomSheetExpanded: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val resolvedColor = MaterialTheme.colorScheme.onSecondary
    val colorOnSecondary = MaterialTheme.colorScheme.surfaceColorAtElevation(2.0.dp)
    val context = LocalContext.current
    val textInfo = if (isAppInfoBottomSheetExpanded) "Open in Settings" else "Info"


    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(Shapes.large),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {

        when (appFilter) {
            AppFilter.ALL -> {
                if (apkInfo.isSystemApp) {
                    ApkItemOptionButton(
                        text = textInfo,
                        modifier = Modifier
                            .weight(1f)
                            .background(color = colorOnSecondary, shape = Shapes.medium)
                    ) {
                        if (isAppInfoBottomSheetExpanded) openAppSettings(
                            context,
                            apkInfo.packageName
                        ) else onButtonClickShowAppInfo?.invoke()
                    }

                } else {
                    ApkItemOptionButton(
                        text = "Open",
                        modifier = Modifier
                            .weight(1f)
                            .background(color = colorOnSecondary, shape = AppShapes.cutLeftMedium)
                    ) {
                        openApp(context, apkInfo.packageName)
                    }
                    ApkItemOptionButton(
                        text = "Uninstall",
                        modifier = Modifier
                            .weight(1f)
                            .background(color = colorOnSecondary)
                    ) {
                        uninstallApp(context, apkInfo.packageName)
                    }

                    ApkItemOptionButton(
                        text = textInfo,
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = colorOnSecondary,
                                shape = AppShapes.cutRightMedium
                            )
                    ) {
                        if (isAppInfoBottomSheetExpanded) openAppSettings(
                            context,
                            apkInfo.packageName
                        ) else onButtonClickShowAppInfo?.invoke()

                    }

                }
            }
            AppFilter.SYSTEM -> {
                ApkItemOptionButton(
                    text = textInfo,
                    modifier = Modifier
                        .weight(1f)
                        .background(color = colorOnSecondary, shape = Shapes.medium)
                ) {
                    if (isAppInfoBottomSheetExpanded) openAppSettings(
                        context,
                        apkInfo.packageName
                    ) else onButtonClickShowAppInfo?.invoke()
                }
            }

            AppFilter.NON_SYSTEM -> {

                ApkItemOptionButton(
                    text = "Open",
                    modifier = Modifier
                        .weight(1f)
                        .background(color = colorOnSecondary, shape = AppShapes.cutLeftMedium)
                ) {
                    openApp(context, apkInfo.packageName)
                }
                ApkItemOptionButton(
                    text = "Uninstall",
                    modifier = Modifier
                        .weight(1f)
                        .background(color = colorOnSecondary)
                ) {
                    uninstallApp(context, apkInfo.packageName)
                }
                ApkItemOptionButton(
                    text = textInfo,
                    modifier = Modifier
                        .weight(1f)
                        .background(color = colorOnSecondary, shape = AppShapes.cutRightMedium)
                ) {
                    if (isAppInfoBottomSheetExpanded) openAppSettings(
                        context,
                        apkInfo.packageName
                    ) else onButtonClickShowAppInfo?.invoke()


                }

            }

            AppFilter.UNINSTALLED_INTERNAL -> {
                ApkItemOptionButton(
                    text = "Install",
                    modifier = Modifier
                        .weight(1f)
                        .background(color = colorOnSecondary, shape = AppShapes.cutLeftMedium)
                ) {
                    apkInfo.apkPath?.let { installApk(context, Paths.get(it)) }
                }
                if (!isAppInfoBottomSheetExpanded) {
                    ApkItemOptionButton(
                        text = "Info",
                        modifier = Modifier
                            .weight(1f)
                            .background(color = colorOnSecondary, shape = AppShapes.cutRightMedium)
                    ) {
                        onButtonClickShowAppInfo?.invoke()

                    }
                }
            }
        }

    }
}

@Composable
fun ApkItemOptionButton(
    text: String, modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .clickable { onClick() }

    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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