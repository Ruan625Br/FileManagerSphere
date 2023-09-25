/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - MediaViewActivity.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.media

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.exoplayer.ExoPlayer
import com.etb.filemanager.files.util.LocaleContextWrapper
import com.etb.filemanager.files.util.toggleOrientation
import com.etb.filemanager.manager.media.model.Media
import com.etb.filemanager.manager.media.model.MediaListInfo
import com.etb.filemanager.manager.media.video.VideoPlayerController
import com.etb.filemanager.ui.theme.FileManagerTheme
import com.etb.filemanager.ui.util.Constants
import com.etb.filemanager.ui.util.Constants.Animation.enterAnimation
import com.etb.filemanager.ui.util.Constants.Animation.exitAnimation
import com.etb.filemanager.ui.util.Constants.DEFAULT_LOW_VELOCITY_SWIPE_DURATION

class MediaViewActivity : ComponentActivity() {
    private var mediaListInfo: MediaListInfo? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        if (bundle != null) {
            mediaListInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("mediaListInfo", MediaListInfo::class.java)
            } else {
                intent.getParcelableExtra("mediaListInfo")
            }
        }
        setContent {
            FileManagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), content = { paddingValues ->
                    if (mediaListInfo != null) {
                        MediaViewScreen(mediaListInfo = mediaListInfo!!,
                            paddingValues = paddingValues,
                            toggleRotate = ::toggleOrientation,
                            navigateUp = { onBackPressedDispatcher.onBackPressed() },)
                    }
                })

            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val context = LocaleContextWrapper.wrap(newBase)
        super.attachBaseContext(context)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaViewScreen(
    mediaListInfo: MediaListInfo,
    paddingValues: PaddingValues,
    toggleRotate: () -> Unit,
    navigateUp: () -> Unit,
) {

    val scrollEnabled = rememberSaveable { mutableStateOf(true) }
    val bottomSheetState = rememberAppBottomSheetState()
    val result =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = {})
    val currentMedia = rememberSaveable { mutableStateOf<Media?>(null) }
    val currentMediaId = mediaListInfo.currentMedia.id
    var runtimeMediaId by rememberSaveable(currentMediaId) { mutableLongStateOf(currentMediaId) }
    val initialPage = rememberSaveable(runtimeMediaId) {
        mediaListInfo.mediaList.indexOfFirst { it.id == runtimeMediaId.coerceAtLeast(0) }
    }

    Log.i("MediaView", "runtimeMediaId: $runtimeMediaId")
    Log.i("MediaView", "currentMediaId: $currentMediaId")

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f,
        pageCount = mediaListInfo.mediaList::size
    )
    val lastIndex = remember { mutableIntStateOf(-1) }
    val updateContent: (Int) -> Unit = { page ->
        if (mediaListInfo.mediaList.isNotEmpty()) {
            val index = if (page == -1) 0 else page
            if (lastIndex.intValue != -1) runtimeMediaId =
                mediaListInfo.mediaList[lastIndex.intValue.coerceAtMost(mediaListInfo.mediaList.size - 1)].id
            currentMedia.value = mediaListInfo.mediaList[index]


        }

    }
    val showUI = rememberSaveable { mutableStateOf(true) }
    val maxImageSize = 4096

    val windowInsetsController = rememberWindowInsetsController()
    val currentDate = rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = scrollEnabled.value,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState, lowVelocityAnimationSpec = tween(
                    easing = LinearEasing, durationMillis = DEFAULT_LOW_VELOCITY_SWIPE_DURATION
                )
            ),
            key = { index -> mediaListInfo.mediaList[index.coerceAtMost(mediaListInfo.mediaList.size - 1)].toString() },
            pageSpacing = 16.dp
        ) { index ->

            MediaPreviewComponent(
                media = mediaListInfo.mediaList[index],
                scrollEnabled = scrollEnabled,
                maxImageSize = maxImageSize,
                playWhenReady = index == pagerState.currentPage,
                onItemClick = {
                    showUI.value = !showUI.value
                    windowInsetsController.toggleSystemBars(showUI.value)
                }) { player: ExoPlayer, currentTime: MutableState<Long>, totalTime: Long, buffer: Int, function: () -> Unit ->

                AnimatedVisibility(
                    visible = showUI.value,
                    enter = enterAnimation(Constants.DEFAULT_TOP_BAR_ANIMATION_DURATION),
                    exit = exitAnimation(Constants.DEFAULT_TOP_BAR_ANIMATION_DURATION),
                    modifier = Modifier.fillMaxSize()
                ) {
                    VideoPlayerController(
                        paddingValues = paddingValues,
                        player = player,
                        currentTime = currentTime,
                        totalTime = totalTime,
                        buffer = buffer,
                        playToggle = function,
                        toggleRotate = toggleRotate
                    )
                }
            }
        }
        MediaViewAppBar(
            showUI = showUI.value,
            showInfo = false,
            showDate = true,
            currentDate = currentDate.value,
            paddingValues = paddingValues,
            bottomSheetState = bottomSheetState,
            onGoBack = navigateUp
        )
        MediaViewBottomBar(
            bottomSheetState = bottomSheetState,
            showUI = showUI.value,
            paddingValues = paddingValues,
            currentMedia = currentMedia.value,
            currentIndex = pagerState.currentPage,
            result = result
        ) {
            lastIndex.intValue = it
        }
    }
    BackHandler(!showUI.value) {
        windowInsetsController.toggleSystemBars(show = true)
        navigateUp()

    }
    LaunchedEffect(mediaListInfo.mediaList) {
            updateContent(pagerState.currentPage)
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
                updateContent(page)
        }
    }


}

@Composable
fun rememberWindowInsetsController(): WindowInsetsControllerCompat {
    val window = with(LocalContext.current as Activity) { return@with window }
    return remember { WindowCompat.getInsetsController(window, window.decorView) }
}

fun WindowInsetsControllerCompat.toggleSystemBars(show: Boolean) {
    if (show) show(WindowInsetsCompat.Type.systemBars())
    else hide(WindowInsetsCompat.Type.systemBars())
}