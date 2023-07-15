package com.etb.filemanager.util.file.style

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.util.Log
import android.widget.ImageView
import com.etb.filemanager.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getBorderPreview(context: Context): Drawable {
        return context.getDrawable(R.drawable.background_border)!!
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getBorderNormal(context: Context): Drawable {
        return context.getDrawable(R.drawable.background_icon_item)!!
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getBackgroundItemSelected(context: Context): Drawable {
        return context.getDrawable(R.drawable.background_file_item_selected)!!
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getBackgroundItemNormal(context: Context): Drawable {
        return context.getDrawable(R.drawable.background_file_item)!!
    }


    suspend fun getBitmapPreviewFromPath(filePath: String, targetWidth: Int, targetHeight: Int): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeFile(filePath, options)

                val scaleFactor = calculateScaleFactor(options.outWidth, options.outHeight, targetWidth, targetHeight)

                val previewOptions = BitmapFactory.Options().apply {
                    inSampleSize = scaleFactor
                }

                return@withContext BitmapFactory.decodeFile(filePath, previewOptions)
            } catch (e: Exception) {
                Log.e("Erro ao obter img", "Erro ${e.message}")
            }

            return@withContext null
        }
    }

    suspend fun getPreview(optionFile: OptionFile, context: Context, filePath: String, imageView: ImageView) {
        when (optionFile) {
            OptionFile.IMAGE -> loadImagePreview(context, filePath, 50, 50, imageView)
            OptionFile.VIDEO -> loadVideoPreview(context, imageView, filePath)
        }
    }

    suspend fun loadImagePreview(
        context: Context,
        filePath: String,
        targetWidth: Int,
        targetHeight: Int,
        imageView: ImageView
    ) {
        val bitmap = getBitmapPreviewFromPath(filePath, targetWidth, targetHeight)
        bitmap?.let {
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    fun calculateScaleFactor(imageWidth: Int, imageHeight: Int, targetWidth: Int, targetHeight: Int): Int {
        var scaleFactor = 1

        if (imageWidth > targetWidth || imageHeight > targetHeight) {
            val widthScale = imageWidth.toFloat() / targetWidth.toFloat()
            val heightScale = imageHeight.toFloat() / targetHeight.toFloat()
            scaleFactor = Math.ceil(Math.max(widthScale, heightScale).toDouble()).toInt()
        }

        return scaleFactor
    }

    suspend fun getVideoPreviewFromPath(context: Context, videoPath: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()

            try {
                // Defina o caminho do vídeo para o MediaMetadataRetriever
                retriever.setDataSource(videoPath)

                // Obtenha o frame do vídeo como um Bitmap
                return@withContext retriever.getFrameAtTime(0)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Libere os recursos do MediaMetadataRetriever
                retriever.release()
            }

            return@withContext null
        }
    }

    suspend fun loadVideoPreview(context: Context, imageView: ImageView, videoPath: String) {
        val bitmap = getVideoPreviewFromPath(context, videoPath)

        bitmap?.let {
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }


    enum class OptionFile() {
        IMAGE,
        VIDEO
    }


}