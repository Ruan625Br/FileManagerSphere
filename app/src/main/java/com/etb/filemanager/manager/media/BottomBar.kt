/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - BottomBar.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.media

/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 */


import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Shapes
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.exifinterface.media.ExifInterface
import com.etb.filemanager.R
import com.etb.filemanager.files.util.FileUtil
import com.etb.filemanager.files.util.actionEdit
import com.etb.filemanager.files.util.shareMedia
import com.etb.filemanager.manager.media.model.Media
import com.etb.filemanager.ui.theme.Black40P
import com.etb.filemanager.ui.util.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.MediaViewBottomBar(
    showDeleteButton: Boolean = true,
    bottomSheetState: AppBottomSheetState,
    showUI: Boolean,
    paddingValues: PaddingValues,
    currentMedia: Media?,
    currentIndex: Int = 0,
    result: ActivityResultLauncher<IntentSenderRequest>? = null,
    onDeleteMedia: ((Int) -> Unit)? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = showUI,
        enter = Constants.Animation.enterAnimation(Constants.DEFAULT_TOP_BAR_ANIMATION_DURATION),
        exit = Constants.Animation.exitAnimation(Constants.DEFAULT_TOP_BAR_ANIMATION_DURATION),
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Black40P)
                    )
                )
                .padding(
                    top = 24.dp,
                    bottom = paddingValues.calculateBottomPadding()
                )
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            if (currentMedia != null) {
                MediaViewActions(
                    currentIndex = currentIndex,
                    currentMedia = currentMedia,
                    onDeleteMedia = onDeleteMedia,
                    result = result,
                    showDeleteButton = showDeleteButton
                )
            }
        }
    }
    currentMedia?.let {
        val exifInterface = remember(currentMedia) {
            getExifInterface(context = context, uri = currentMedia.uri)
        }
        if (exifInterface != null) {
            val exifMetadata = remember(currentMedia) {
                ExifMetadata(exifInterface)
            }
            if (bottomSheetState.isVisible) {
                ModalBottomSheet(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    onDismissRequest = {
                        scope.launch {
                            bottomSheetState.hide()
                        }
                    },
                    shape = Shapes().extraSmall,
                    sheetState = bottomSheetState.sheetState,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    dragHandle = null
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        MediaViewDateContainer(
                            exifMetadata = exifMetadata
                        ) {
/**/
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        BackHandler(bottomSheetState.isVisible) {
            scope.launch {
                bottomSheetState.hide()
            }
        }
    }
}

@Composable
private fun MediaViewDateContainer(
    exifMetadata: ExifMetadata,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes().extraLarge)
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
                }
            }
            Text(
                text = exifMetadata.imageDescription
                    ?: stringResource(R.string.category_add),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )
        }
        content()
    }
}

@Composable
private fun MediaViewActions(
    currentIndex: Int,
    currentMedia: Media,
    onDeleteMedia: ((Int) -> Unit)?,
    result: ActivityResultLauncher<IntentSenderRequest>?,
    showDeleteButton: Boolean
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Share Component
    BottomBarColumn(
        currentMedia = currentMedia,
        imageVector = Icons.Outlined.Share,
        title = stringResource(R.string.share)
    ) {
        context.shareMedia(media = it)
    }
    // Edit
    BottomBarColumn(
        currentMedia = currentMedia,
        imageVector = Icons.Outlined.Edit,
        title = stringResource(id = R.string.edit)
    ) {
        scope.launch { it.uri.path?.let { it1 -> context.actionEdit(it1) } }
    }
    // Open With

    BottomBarColumn(
        currentMedia = currentMedia,
        imageVector = Icons.Outlined.OpenInNew,
        title = stringResource(R.string.open_with)
    ) {
        scope.launch { it.uri.path?.let { it1 -> FileUtil().actionOpenWith(it1, context) } }
    }

    if (showDeleteButton) {
        // Trash Component
        BottomBarColumn(
            currentMedia = currentMedia,
            imageVector = Icons.Outlined.DeleteOutline,
            title = stringResource(id = R.string.delete)
        ) {
            result?.let { _ ->
                scope.launch {
                    onDeleteMedia?.invoke(currentIndex)
                }
            }
        }
    }
}

@Composable
fun BottomBarColumn(
    currentMedia: Media?,
    imageVector: ImageVector,
    title: String,
    onItemClick: (Media) -> Unit
) {
    val tintColor = Color.White
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .defaultMinSize(
                minWidth = 90.dp,
                minHeight = 80.dp
            )
            .clickable {
                currentMedia?.let {
                    onItemClick.invoke(it)
                }
            }
            .padding(top = 12.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = imageVector,
            colorFilter = ColorFilter.tint(tintColor),
            contentDescription = title,
            modifier = Modifier
                .height(32.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = title,
            modifier = Modifier,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium,
            color = tintColor,
            textAlign = TextAlign.Center
        )
    }
}


//AppBottomSheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAppBottomSheetState(): AppBottomSheetState {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    return rememberSaveable(saver = AppBottomSheetState.Saver()) {
        AppBottomSheetState(sheetState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class AppBottomSheetState(
    val sheetState: SheetState
) {

    var isVisible by mutableStateOf(false)
        private set

    internal constructor(sheetState: SheetState, isVisible: Boolean) : this(sheetState) {
        this.isVisible = isVisible
    }

    suspend fun show() {
        if (!isVisible) {
            isVisible = true
            delay(10)
            sheetState.show()
        }
    }

    suspend fun hide() {
        if (isVisible) {
            sheetState.hide()
            delay(10)
            isVisible = false
        }
    }

    companion object {
        fun Saver(
            skipPartiallyExpanded: Boolean = true,
            confirmValueChange: (SheetValue) -> Boolean = { true }
        ) = androidx.compose.runtime.saveable.Saver<AppBottomSheetState, Pair<SheetValue, Boolean>>(
            save = { Pair(it.sheetState.currentValue, it.isVisible) },
            restore = { savedValue ->
                AppBottomSheetState(
                    SheetState(skipPartiallyExpanded, savedValue.first, confirmValueChange),
                    savedValue.second
                )
            }
        )
    }
}

class ExifMetadata(exifInterface: ExifInterface) {
    private val imageWidth: Int =
        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, -1)
    private val imageHeight: Int =
        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, -1)

    val imageDescription: String? =
        exifInterface.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION)

}

@Throws(IOException::class)
fun getExifInterface(context: Context, uri: Uri): ExifInterface? {
    return try {
        // ExifInterface(uri.fileProviderPath.toFile())
        return null
    } catch (_: IOException) {
        null
    }
}