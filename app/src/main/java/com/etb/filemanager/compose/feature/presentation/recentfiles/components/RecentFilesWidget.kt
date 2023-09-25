/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - RecentFilesWidget.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.recentfiles.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.etb.filemanager.R
import com.etb.filemanager.activity.SettingsActivity
import com.etb.filemanager.compose.core.presentation.components.CircularProgressBar
import com.etb.filemanager.compose.core.presentation.components.ProgressBar
import com.etb.filemanager.compose.feature.presentation.recentimages.model.RecentImage
import com.etb.filemanager.compose.feature.presentation.storage.model.StorageItem
import com.etb.filemanager.files.util.fetchRecentImagesUris
import com.etb.filemanager.manager.category.adapter.CategoryFileModel
import com.etb.filemanager.manager.category.adapter.getCategories
import com.etb.filemanager.settings.preference.Preferences
import com.etb.filemanager.ui.theme.AppShapes
import com.etb.filemanager.ui.theme.FileManagerTheme
import com.etb.filemanager.ui.theme.Shapes
import java.nio.file.Paths
import kotlin.io.path.pathString


@Composable
fun RecentFilesWidget() {
    FileManagerTheme {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 80.dp)
        ) {
            MobileInfoWidget()
            Spacer(modifier = Modifier.size(16.dp))

            StorageListWidget()
            Spacer(modifier = Modifier.size(16.dp))

            CategoryFilesWidget()
            Spacer(modifier = Modifier.size(16.dp))
            RecentImagesWidget()

        }

    }
}

@Composable
fun CategoryFilesWidget() {
    CardWidget(
        modifier = Modifier
            .height(230.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp)

        ) {
            CategoryFilesList(categoryFilesList = getCategories(LocalContext.current))
            Row(
                horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = stringResource(id = R.string.category_add),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(30.dp)
                    )
                }

            }

        }



    }


}

@Composable
fun MobileInfoWidget() {
    FileManagerTheme {

        CardWidget(modifier = Modifier.height(199.dp)) {
            InfoStorage()
        }
    }
}

@Composable
fun StorageListWidget() {


    val storageList = mutableListOf<StorageItem>()
    storageList.add(
        StorageItem(
            Paths.get(
                Preferences.Behavior.defaultFolder,
                stringResource(id = R.string.internal_st)
            )
        )
    )
    CardWidget(
        modifier = Modifier.height(85.dp),
    ) {
        StorageList(storageItemList = storageList)
    }
}


@Composable
fun InfoStorage() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(52.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                CardWithText(text = stringResource(id = R.string.used_space))
                Text(text = "85 GB",
                    color = MaterialTheme.colorScheme.inverseOnSurface)
            }

            Spacer(modifier = Modifier.size(32.dp))
            Column(
                modifier = Modifier
                    .padding(bottom = 15.dp)
            ){
                CircularProgressBar(
                    progress = 80.0f,
                    text = "108",
                    subText = stringResource(id = R.string.gb),
                    strokeWidth = 10.dp,
                    modifier = Modifier
                        .size(95.dp)
                )

            }

            Spacer(modifier = Modifier.size(32.dp))


            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CardWithText(text = stringResource(id = R.string.free_space))
                Text(text = "25 GB", color = MaterialTheme.colorScheme.inverseOnSurface)
            }


        }
        Row(
            horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
        ) {

            IconButton(
                onClick = {
                    val settingsIntent: Intent = SettingsActivity().getIntent(context)
                    context.startActivity(settingsIntent)
                }) {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }

        }


    }
}

@Composable
private fun CardWithText(text: String) {
    Card(
        modifier = Modifier.size(width = 80.dp, height = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.onPrimary
                )
        ) {
            Text(
                text = text,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.inverseSurface,
                modifier = Modifier.align(alignment = Alignment.Center)

            )
        }
    }

}

@Composable
fun CategoryFilesList(categoryFilesList: List<CategoryFileModel>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4), verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(categoryFilesList) { category ->
            CategoryItem(category = category)
        }

    }
}

@Composable
fun CategoryItem(category: CategoryFileModel) {
    val imageResId = category.icon


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .size(width = 62.dp, 62.dp)
                .clip(AppShapes.rounded.Medium)


        ) {
            Box(
                modifier = Modifier
                    .size(width = 62.dp, 62.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "FileItemImage",
                    modifier = Modifier
                        .size(width = 28.dp, height = 28.dp)
                        .align(alignment = Alignment.Center),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = category.title,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.inverseOnSurface

        )

    }
}


@Composable
fun StorageList(storageItemList: List<StorageItem>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),

        ) {
        items(items = storageItemList, key = { it.path.pathString }) { storage ->
            ProcessStorageItem(storage = storage)
        }

    }
}

@Composable
fun ProcessStorageItem(storage: StorageItem) {

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(0.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.PhoneAndroid,
                contentDescription = storage.name,
                tint = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier.size(17.dp)
            )
            Text(
                text = storage.name,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.size(4.dp))

        ProgressBar(progress = 70.0f)
        Spacer(modifier = Modifier.size(4.dp))

        Row {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)

            ) {
                Text(
                    text = "Free Space: 25 GB of 128",
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    fontSize = 11.sp
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(0.dp)

            ) {
                Text(
                    text = "Explore",
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    fontSize = 11.sp
                )
            }


        }
    }
}

@Composable
fun RecentImagesWidget() {
    val recentImagesUriList = remember { mutableStateListOf<RecentImage>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val images = fetchRecentImagesUris(context)
        recentImagesUriList.addAll(images)
    }

    CardWidget(
    ) {
       RecentImagesList(recentImagesList = recentImagesUriList)
    }
}


@Composable
fun RecentImagesList(recentImagesList: MutableList<RecentImage>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(recentImagesList, key = { it.imageUri.toString() }) { image ->
            RecentImageItem(image = image)
        }

    }

}

@Composable
fun RecentImageItem(image: RecentImage, modifier: Modifier = Modifier) {
    val label = image.imageUri.toString().substringAfterLast("/")

    val key = "image_${label}"
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(image.imageUri)
            .memoryCacheKey(key)
            .diskCacheKey(key)
            .build(),
        contentScale = ContentScale.Crop,
        filterQuality = FilterQuality.None,
    )

    Image(
        modifier = modifier
            .size(width = 110.dp, height = 101.dp)
            .clip(Shapes.medium),
        painter = painter,
        contentScale = ContentScale.Crop,
        contentDescription = null
    )


}

@Composable
fun CardWidget(
    modifier: Modifier = Modifier,
    columModifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(AppShapes.shapeFromPreferences)

    ) {
        Column(
            modifier = columModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(AppShapes.shapeFromPreferences)

                .background(
                    color = MaterialTheme.colorScheme.primary
                )
        ) {
            content()

        }
    }
}

@Preview
@Composable
fun PreviewCategoryList() {
    RecentFilesWidget()
}