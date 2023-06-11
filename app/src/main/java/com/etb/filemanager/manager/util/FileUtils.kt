package com.etb.filemanager.manager.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import com.etb.filemanager.R
import com.etb.filemanager.manager.category.adapter.RecentImageModel
import com.etb.filemanager.settings.preference.PopupSettings
import com.etb.filemanager.util.file.style.ColorUtil
import com.etb.filemanager.util.file.style.IconUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class FileUtils {

    private val BASE_PATH = "/storage/emulated/0"
    private val colorUtil = ColorUtil()
    private val iconUtil = IconUtil()

    fun getFileExtension(file: File?): String {
        var extension = ""
        if (file != null && file.exists()) {
            val fileName = file.name
            val dotIndex = fileName.lastIndexOf('.')
            if (dotIndex > 0 && dotIndex < fileName.length) {
                extension = fileName.substring(dotIndex + 1)
            }
        }
        return extension
    }

    fun getFileSize(file: File?): Long {
        var fileSize: Long = 0
        if (file != null && file.exists()) {
            fileSize = file.length()
        }
        return fileSize
    }

    fun deleteFile(file: File?): Boolean {
        var deleted = false
        if (file != null && file.exists()) {
            deleted = file.delete()
        }
        return deleted
    }

    fun getIconApk(context: Context, apkFilePath: String?): Drawable? {
        try {
            val pm = context.packageManager
            val packageInfo = pm.getPackageArchiveInfo(apkFilePath!!, 0)
            val appInfo = packageInfo!!.applicationInfo
            appInfo.sourceDir = apkFilePath
            appInfo.publicSourceDir = apkFilePath
            return appInfo.loadIcon(pm)
        } catch (e: Exception){

            return iconUtil.getIconFolder(context)
        }
    }

    fun formatSizeFile(fileSize: Long): String {
        if (fileSize <= 0) {
            return "0 B"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(fileSize.toDouble()) / Math.log10(1024.0)).toInt()
        return (DecimalFormat("#,##0.#").format(fileSize / Math.pow(1024.0, digitGroups.toDouble()))
                + " "
                + units[digitGroups])
    }

    // get date file

    // get date file
    fun formatDateShort(date: Date): String {
        val dateFormat = SimpleDateFormat("d 'de' MMM", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun formatDateLong(date: Date): String {
        val dateFormat = SimpleDateFormat("d 'de' MMM. 'de' yyyy HH:mm:ss", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getFormatDateFile(path: String, isShort: Boolean): String {
        val file = File(path)
        if (!file.isFile) {
            val lastModified = file.lastModified()
            val date = Date(lastModified)
            val formattedDate = formatDateShort(date)
            val formattedDateLong = formatDateLong(date)
            return if (isShort) {
                formattedDate
            } else {
                formattedDateLong
            }
        }
        val lastModified = Date(file.lastModified())
        val formattedDateShort = formatDateShort(lastModified)
        val formattedDateLong = formatDateLong(lastModified)
        return if (isShort) {
            formattedDateShort
        } else {
            formattedDateLong
        }
    }

    fun getFileSizeFormatted(fileSize: Long): String {
        return formatSizeFile(fileSize)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getFileIconByExtension(context: Context, file: File): Drawable? {

        return when {
            fileIsApk(file) -> getIconApk(context, file.absolutePath)
            fileIsArchive(file) -> iconUtil.getIconArchive(context)
           // isImage(file) -> iconUtil.getDrawablePreviewFromPath(context, file.absolutePath, 29, 30)
          //  isVideo(file) -> iconUtil.getVideoPreviewFromPath(context, file.absolutePath)
            else -> iconUtil.getIconFolder(context)
        }
    }

    private fun fileIsApk(file: File): Boolean {
        val fileExtension = getFileExtension(file)
        return fileExtension == "apk"
    }

    private fun fileIsArchive(file: File): Boolean {
        val fileExtension = getFileExtension(file)
        return isArchive(fileExtension)
    }

    private fun isImage(file: File): Boolean{
        val fileExtension = getFileExtension(file)
        return fileExtension in listOf("png", "jpg", "jpeg")
    }

    private fun isVideo(file: File): Boolean{
        val fileExtension = getFileExtension(file)
        return fileExtension in listOf("mp4")
    }


/*
        fun getFileIconByPath(context: Context, imagePath: String): Drawable? {
            val file = File(imagePath)
            if (file.exists() && file.isFile) {
                val mimeType = getMimeType(imagePath)
                return getDrawableForMimeType(context, mimeType)
            }
            return null
        }*/

        private fun getMimeType(filePath: String): String? {
            val extension = MimeTypeMap.getFileExtensionFromUrl(filePath)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }

       /* private fun getDrawableForMimeType(context: Context, mimeType: String?): Drawable? {
            return try {
                val iconRes = when (mimeType) {
                    "image/jpeg", "image/png" -> R.drawable.ic_image
                    "video/mp4" -> R.drawable.ic_video
                    "audio/mp3" -> R.drawable.ic_audio
                    // Add more cases for other mime types if needed
                    else -> R.drawable.ic_file
                }
                ContextCompat.getDrawable(context, iconRes)
            } catch (e: Exception) {
                null
            }
        }*/



    private fun isArchive(fileExtension: String): Boolean {
        return fileExtension in listOf("zip", "7z", "tar", "tar.bz2", "tar.gz", "tar.xz", "tar.lz4", "tar.zstd")
    }

    fun isShowHiddenFiles(context: Context): Boolean {
        val popupSettings: PopupSettings = PopupSettings(context)
        return popupSettings.getActionShowHiddenFiles()
    }

    fun getStorageSpaceInGB(spaceType: SpaceType): Int {
        val file = File(BASE_PATH)
        val totalSpace = file.totalSpace
        val freeSpace = file.freeSpace
        val usedSpace = totalSpace - freeSpace

        return when (spaceType) {
            SpaceType.TOTAL -> convertBytesToGB(totalSpace)
            SpaceType.FREE -> convertBytesToGB(freeSpace)
            SpaceType.USED -> convertBytesToGB(usedSpace)
        }
    }

    private fun convertBytesToGB(bytes: Long): Int {
        val gb = bytes / (1024 * 1024 * 1024)
        return gb.toInt()
    }

    enum class SpaceType {
        TOTAL,
        FREE,
        USED
    }

    enum class CreationOption {
        FILE,
        FOLDER
    }


    fun createFileAndFolder(path: String, name: String, creationOption: CreationOption): Boolean {
        return when (creationOption) {
            CreationOption.FOLDER -> createFolder(path, name)
            CreationOption.FILE -> createFile(path, name)
        }
    }


    fun createFolder(folderPath: String, folderName: String): Boolean {
        val folder = File(folderPath, folderName)
        return if (!folder.exists() && folder.mkdirs()) {
            true // Pasta criada com sucesso
        } else {
            false // A pasta já existe ou ocorreu um erro ao criá-la
        }
    }

    fun createFile(filePath: String, fileName: String): Boolean {
        val file = File(filePath, fileName)

        return try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false // Ocorreu um erro ao criar o arquivo
        }
    }


    suspend fun getRecentImages(context: Context): ArrayList<RecentImageModel> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val recentImageModelList = ArrayList<RecentImageModel>()

        withContext(Dispatchers.IO) {
            context.contentResolver.query(queryUri, projection, null, null, sortOrder)?.use { cursor ->
                val imagePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                var count = 0
                while (cursor.moveToNext() && count < 11) {
                    val imagePath = cursor.getString(imagePathColumn)
                    recentImageModelList.add(RecentImageModel(imagePath))
                    count++
                }
            }
        }

        recentImageModelList
    }






    companion object {
        private var instance: FileUtils? = null

        fun getInstance(): FileUtils {
            if (instance == null) {
                instance = FileUtils()
            }
            return instance!!
        }
    }
}