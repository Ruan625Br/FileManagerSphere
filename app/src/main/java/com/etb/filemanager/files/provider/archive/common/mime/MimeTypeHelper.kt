/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - MimeTypeHelper.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.provider.archive.common.mime
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.webkit.MimeTypeMap
import com.etb.filemanager.R

class MimeTypeHelper(private val context: Context) {

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getIconForMimeType(mimeType: String): Drawable? {
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        val resourceId = getDrawableResourceId(fileExtension)
        return if (resourceId != 0) context.getDrawable(resourceId) else null
    }

    private fun getDrawableResourceId(fileExtension: String?): Int {
        return when (fileExtension) {
            "apk" -> R.drawable.file_apk_icon
            "jpg", "jpeg", "png", "gif" -> R.drawable.ic_image
            // Adicione outros MIME types e seus respectivos ícones aqui
            else -> 0
        }
    }
}
