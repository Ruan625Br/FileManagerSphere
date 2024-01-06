/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - IconUtil.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.ui.style

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.widget.ImageView
import com.etb.filemanager.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ceil

class IconUtil {
    private val colorUtil = ColorUtil()

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getIconFolder(context: Context): Drawable {
        val colorPrimaryInverse = colorUtil.getColorPrimaryInverse(context)
        val iconFolder: Drawable = context.getDrawable(R.drawable.ic_folder)!!
        colorUtil.setTintDrawable(colorPrimaryInverse, iconFolder)

        return iconFolder
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getPreviewImage(context: Context): Drawable {
        val colorPrimaryInverse = colorUtil.getColorPrimaryInverse(context)
        val iconFolder: Drawable = context.getDrawable(R.drawable.file_image_icon)!!
        colorUtil.setTintDrawable(colorPrimaryInverse, iconFolder)

        return iconFolder
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getPreviewVideo(context: Context): Drawable {
        val colorPrimaryInverse = colorUtil.getColorPrimaryInverse(context)
        val iconFolder: Drawable = context.getDrawable(R.drawable.file_video_icon)!!
        colorUtil.setTintDrawable(colorPrimaryInverse, iconFolder)

        return iconFolder
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getIconArchive(context: Context): Drawable {
        val colorPrimaryInverse = colorUtil.getColorPrimaryInverse(context)
        val iconArchive: Drawable = context.getDrawable(R.drawable.ic_archive)!!
        colorUtil.setTintDrawable(colorPrimaryInverse, iconArchive)

        return iconArchive
    }

    private suspend fun getBitmapPreviewFromPath(
        filePath: String,
        targetWidth: Int,
        targetHeight: Int
    ): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeFile(filePath, options)

                val scaleFactor = calculateScaleFactor(
                    options.outWidth,
                    options.outHeight,
                    targetWidth,
                    targetHeight
                )

                val previewOptions = BitmapFactory.Options().apply {
                    inSampleSize = scaleFactor
                }

                return@withContext BitmapFactory.decodeFile(filePath, previewOptions)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext null
        }
    }

    private suspend fun loadImagePreview(
        filePath: String, targetWidth: Int, targetHeight: Int, imageView: ImageView
    ) {
        val bitmap = getBitmapPreviewFromPath(filePath, targetWidth, targetHeight)
        bitmap?.let {
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun calculateScaleFactor(
        imageWidth: Int,
        imageHeight: Int,
        targetWidth: Int,
        targetHeight: Int
    ): Int {
        var scaleFactor = 1

        if (imageWidth > targetWidth || imageHeight > targetHeight) {
            val widthScale = imageWidth.toFloat() / targetWidth.toFloat()
            val heightScale = imageHeight.toFloat() / targetHeight.toFloat()
            scaleFactor = ceil(widthScale.coerceAtLeast(heightScale).toDouble()).toInt()
        }

        return scaleFactor
    }

    private suspend fun getVideoPreviewFromPath(videoPath: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()

            try {
                retriever.setDataSource(videoPath)
                return@withContext retriever.getFrameAtTime(0)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                retriever.release()
            }

            return@withContext null
        }
    }

    private suspend fun loadVideoPreview(imageView: ImageView, videoPath: String) {
        val bitmap = getVideoPreviewFromPath(videoPath)

        bitmap?.let {
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }


    enum class OptionFile {
        IMAGE, VIDEO
    }


}