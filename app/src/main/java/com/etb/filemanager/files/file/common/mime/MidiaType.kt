package com.etb.filemanager.files.file.common.mime

enum class MidiaType {
    IMAGE,
    VIDEO,
    MUSIC,
    APK
}

fun getMidiaType(mimeType: String): MidiaType? {
    val midiaTypeToMap = mapOf(
        "image/png" to MidiaType.IMAGE,
        "video/mp4" to MidiaType.VIDEO,
        "audio/mp3" to MidiaType.MUSIC,
        "application/vnd.android.package-archive" to MidiaType.APK
    )
    return midiaTypeToMap[mimeType]
}