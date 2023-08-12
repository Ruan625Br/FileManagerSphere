package com.etb.filemanager.manager.media.model

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import com.etb.filemanager.files.provider.archive.common.mime.MimeType
import com.etb.filemanager.files.util.FileUtil
import com.etb.filemanager.files.util.getMediaIdFromPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
data class Media(
    val uri: Uri,
    val id: Long = 0,
    val mimeType: MimeType
) : Parcelable {
    override fun toString(): String {
        return "$uri"
    }

    companion object {
       suspend fun createFromUri(uri: Uri, context: Context): Media {
            val mime = FileUtil().getMimeType(uri, null)!!
            val mimeType = MimeType(mime)
            val mId = getMediaIdFromPath(uri.path!!, context)
            val id = mId ?: 0

            return Media(
                uri = uri,
                id = id,
                mimeType = mimeType
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
