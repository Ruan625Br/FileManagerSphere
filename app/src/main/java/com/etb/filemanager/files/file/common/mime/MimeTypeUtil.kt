package com.etb.filemanager.files.file.common.mime

import android.graphics.BitmapFactory
import android.util.Log
import com.etb.filemanager.R
import java.util.*

class MimeTypeUtil {


    fun getIconByMimeType(mimeType: String, path: String): Int {
        val mimeTypeObj = MimeType(mimeType)

        Log.e("TYPE", "Tipo mime $mimeTypeObj")

        val icon: MimeTypeIcon = mimeTypeObj.icon
        val iconResourceId: Int = icon.resourceId
        return iconResourceId


    }

    private fun getIconMidia(mimeType: String, path: String): Int {

        Log.e("TYPE", "Tipo mime $mimeType")

        if (mimeType.isMimeTypeImage()) {

            return getResourcePreviewFromPath(path, 73, 73)
        }


        return R.drawable.file_audio_icon
    }

    private fun getResourcePreviewFromPath(path: String, width: Int, height: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        val scaleFactor = calculateScaleFactor(options.outWidth, options.outHeight, width, height)

        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor

        val bitmap = BitmapFactory.decodeFile(path, options)
        val pixel = bitmap?.getPixel(0, 0) ?: 0
        bitmap?.recycle()

        return pixel
    }

    private fun calculateScaleFactor(imageWidth: Int, imageHeight: Int, targetWidth: Int, targetHeight: Int): Int {
        var scaleFactor = 1

        if (imageWidth > targetWidth || imageHeight > targetHeight) {
            val widthScale = Math.round(imageWidth.toFloat() / targetWidth.toFloat())
            val heightScale = Math.round(imageHeight.toFloat() / targetHeight.toFloat())
            scaleFactor = if (widthScale < heightScale) widthScale else heightScale
        }

        return scaleFactor
    }


    private fun String.isMimeTypeMedia(): Boolean {
        val mediaMimeTypes = listOf("video/", "audio/", "image/", "apk")
        val mimeType = this.lowercase(Locale.getDefault())
        return mediaMimeTypes.any { mimeType.startsWith(it) }
    }


    private fun String.isMimeTypeImage(): Boolean {
        return this.lowercase(Locale.getDefault()).startsWith("image/")
    }


}