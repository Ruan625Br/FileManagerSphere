package com.etb.filemanager.manager.media.model

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import com.etb.filemanager.files.provider.archive.common.mime.MimeType
import com.etb.filemanager.files.util.FileUtil
import com.etb.filemanager.files.util.fileProviderUri
import com.etb.filemanager.files.util.getMediaIdFromPath
import com.etb.filemanager.manager.adapter.FileModel
import kotlinx.parcelize.Parcelize
import java.nio.file.Paths
import java.util.Locale

@Parcelize
data class Media(
    val uri: Uri,
    val id: Long = 0,
    val mimeType: MimeType
) : Parcelable {
    override fun toString(): String {
        return "id=$id, uri=$uri, mimeType=$mimeType"
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

         fun createFromFileModel(file: FileModel): Media {
            val uri = Paths.get(file.filePath).fileProviderUri
            val mime = FileUtil().getMimeType(uri, null)!!
            val mimeType = MimeType(mime)
             val id = if (file.id.toString().startsWith("-")) {
                 file.id.toString().removePrefix("-").toLong()
             } else {
                 file.id
             }


             Log.i("Media", "Id: $id")

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
