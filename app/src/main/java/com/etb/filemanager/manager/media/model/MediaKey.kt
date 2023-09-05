/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - MediaKey.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.media.model

import android.os.Parcelable
import com.bumptech.glide.load.Key
import com.etb.filemanager.files.provider.archive.common.mime.MimeType
import kotlinx.parcelize.Parcelize
import java.nio.ByteBuffer
import java.security.MessageDigest

@Parcelize
data class MediaKey(
    val id: Long = 0,
    val mimeType: MimeType
): Key, Parcelable {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        val data = ByteBuffer.allocate(20)
            .putLong(id)
        messageDigest.update(data)
        messageDigest.update(mimeType.value.toByteArray(Key.CHARSET))
    }
}