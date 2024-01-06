/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - Media.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.media.model

import android.net.Uri
import android.os.Parcelable
import com.etb.filemanager.files.provider.archive.common.mime.MimeType
import com.etb.filemanager.files.util.FileUtil
import com.etb.filemanager.files.util.fileProviderUri
import com.etb.filemanager.manager.adapter.FileModel
import kotlinx.parcelize.Parcelize
import java.nio.file.Paths

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
         fun createFromFileModel(file: FileModel): Media {
            val uri = Paths.get(file.filePath).fileProviderUri
            val mime = FileUtil().getMimeType(uri, null)!!
            val mimeType = MimeType(mime)
             val id = if (file.id.toString().startsWith("-")) {
                 file.id.toString().removePrefix("-").toLong()
             } else {
                 file.id
             }


            return Media(
                uri = uri,
                id = id,
                mimeType = mimeType
            )
        }
    }
}
