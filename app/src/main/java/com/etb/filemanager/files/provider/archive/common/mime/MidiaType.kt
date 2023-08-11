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
