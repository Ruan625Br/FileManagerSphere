package com.etb.filemanager.manager.media.model

import android.net.Uri
import android.os.Parcelable
import com.etb.filemanager.files.provider.archive.common.mime.MimeType
import kotlinx.parcelize.Parcelize

@Parcelize
class Media(
    val uri: Uri,
) : Parcelable {
    override fun toString(): String {
        return "$uri"
    }


}