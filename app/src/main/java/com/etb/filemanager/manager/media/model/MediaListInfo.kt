/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - MediaListInfo.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaListInfo(
    val mediaList: List<Media>,
    val currentMedia: Media
): Parcelable

fun List<Media>.toMediaListInfo(currentMedia: Media) =
    MediaListInfo(this, currentMedia)
