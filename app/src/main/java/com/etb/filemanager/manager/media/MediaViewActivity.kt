package com.etb.filemanager.manager.media

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.exoplayer.ExoPlayer
import com.etb.filemanager.files.util.toggleOrientation
import com.etb.filemanager.manager.media.model.Media
import com.etb.filemanager.manager.media.ui.theme.FileManagerTheme
import com.etb.filemanager.manager.media.video.VideoPlayerController
import com.etb.filemanager.ui.util.Constants
import com.etb.filemanager.ui.util.Constants.Animation.enterAnimation
import com.etb.filemanager.ui.util.Constants.Animation.exitAnimation

class MediaViewActivity : ComponentActivity() {
    private var media: Media? = null
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        if (bundle != null) {
            media = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("media", Media::class.java)
            } else {
                intent.getParcelableExtra("media")
            }
        }
        setContent {
            FileManagerTheme {
                val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { paddingValues ->
                        if (media != null) {
                            MediaViewScreen(
                                media = media!!,
                                paddingValues = paddingValues,
                                toggleRotate = ::toggleOrientation
                            )
                        }
                    }
                )

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true, device = "id:pixel_7_pro")
@Composable
fun GreetingPreview() {
    FileManagerTheme {
        Greeting("Android")
    }
}
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MediaViewScreen(
        media: Media,
        paddingValues: PaddingValues,
        toggleRotate: () -> Unit) {

        val scrollEnabled = rememberSaveable { mutableStateOf(true) }
        val bottomSheetState = rememberAppBottomSheetState()


        val showUI = rememberSaveable { mutableStateOf(true) }
        val windowInsetsController = rememberWindowInsetsController()
        val currentDate = rememberSaveable { mutableStateOf("") }

        Box(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
        ) {

            MediaPreviewComponent(
                media = media,
                playWhenReady = true,
                onItemClick = {
                    showUI.value = !showUI.value
                    windowInsetsController.toggleSystemBars(showUI.value)
                })
            { player: ExoPlayer, currentTime: MutableState<Long>, totalTime: Long, buffer: Int, function: () -> Unit ->

                AnimatedVisibility(
                    visible = showUI.value,
                    enter = enterAnimation(Constants.DEFAULT_TOP_BAR_ANIMATION_DURATION),
                    exit = exitAnimation(Constants.DEFAULT_TOP_BAR_ANIMATION_DURATION),
                    modifier = Modifier.fillMaxSize()
                )
                {
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
            MediaViewAppBar(
                showUI = showUI.value,
                showInfo = false,
                showDate = true,
                currentDate = currentDate.value,
                paddingValues = paddingValues,
                bottomSheetState = bottomSheetState,
                onGoBack = {}
            )
            MediaViewBottomBar(
                bottomSheetState = bottomSheetState,
                showUI = showUI.value,
                paddingValues = paddingValues,
                currentMedia = media
            )
        }
        BackHandler(!showUI.value) {
            windowInsetsController.toggleSystemBars(show = true)

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