package com.etb.filemanager.manager.media

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer
import com.etb.filemanager.files.provider.archive.common.mime.MimeType
import com.etb.filemanager.files.provider.archive.common.mime.isASpecificTypeOfMime
import com.etb.filemanager.manager.media.image.ZoomablePagerImage
import com.etb.filemanager.manager.media.model.Media
import com.etb.filemanager.manager.media.video.VideoPlayer

@Composable
fun MediaPreviewComponent(
    media: Media,
    scrollEnabled: MutableState<Boolean>,
    maxImageSize: Int,
    playWhenReady: Boolean,
    onItemClick: () -> Unit,
    videoController: @Composable (ExoPlayer, MutableState<Long>, Long, Int, () -> Unit) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (media.mimeType.isASpecificTypeOfMime(MimeType.VIDEO_MP4)) {
            VideoPlayer(
                media = media,
                playWhenReady = playWhenReady,
                videoController = videoController,
                onItemClick = onItemClick
            )
        } else {
            ZoomablePagerImage(
                media = media,
                scrollEnabled = scrollEnabled,
                maxImageSize = maxImageSize,
                onItemClick = onItemClick
            )
        }
    }
}