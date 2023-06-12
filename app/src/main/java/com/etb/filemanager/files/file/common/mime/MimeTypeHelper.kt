package com.etb.filemanager.files.file.common.mime
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
