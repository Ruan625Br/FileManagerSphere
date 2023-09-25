/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - MidiaType.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.provider.archive.common.mime

enum class MediaType(val displayName: String) {
    IMAGE("Imagem"),
    VIDEO("Video"),
    MUSIC("Musica"),
    APK("Aplicativo")
}

fun getMidiaType(mimeType: String): MediaType? {
    val midiaTypeToMap = mapOf(
        "image/png" to MediaType.IMAGE,
        "video/mp4" to MediaType.VIDEO,
        "audio/mp3" to MediaType.MUSIC,
        "application/vnd.android.package-archive" to MediaType.APK
    )
    return midiaTypeToMap[mimeType]
}
