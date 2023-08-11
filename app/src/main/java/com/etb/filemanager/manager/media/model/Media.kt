package com.etb.filemanager.manager.media.model

import android.net.Uri
import android.os.Parcelable
import com.etb.filemanager.files.provider.archive.common.mime.MimeType
import com.etb.filemanager.files.util.FileUtil
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
class Media(
    val uri: Uri,
    val mimeType: MimeType,
) : Parcelable {
    override fun toString(): String {
        return "$uri"
    }

    companion object {
        fun createFromUri(uri: Uri): Media {
            val mime = FileUtil().getMimeType(uri, null)!!
            val mimeType = MimeType(mime)
            return Media(
                uri = uri,
                mimeType
            )
        }
    }
}

fun Uri.isMediaMimeType(): Boolean {
    val path = this.path!!
    val mediaMimeTypes = listOf("video/", "image/")
    val mimeType = path.lowercase(Locale.getDefault())
    return mediaMimeTypes.any { mimeType.startsWith(it) }
}
