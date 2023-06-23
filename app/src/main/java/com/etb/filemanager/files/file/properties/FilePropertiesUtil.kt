package com.etb.filemanager.files.file.properties

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.fragment.app.Fragment
import com.etb.filemanager.files.file.common.mime.MidiaType
import com.etb.filemanager.files.file.common.mime.getMidiaType
import com.etb.filemanager.fragment.HomeFragment
import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.manager.util.FileUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit

class FilePropertiesUtil {




    private fun addTabTitle(title: String) {
        HomeFragment().propertiesViewModel.addNewTab(title)
    }


    fun getFileProperties(fileItem: FileModel): Fragment {
        val mimeType = getFileMimeType(fileItem.filePath)

        if (fileItem.isDirectory || mimeType == null || !mimeType.isMimeTypeMedia()) {
            return getBasicProperties(fileItem)
        }

        val mediaType = getMidiaType(mimeType)
        return when (mediaType) {
            MidiaType.IMAGE -> getImageProperties(fileItem, mediaType)
            MidiaType.VIDEO -> getVideoProperties(fileItem, mediaType)
            else -> mediaType?.let { getImageProperties(fileItem, it) }!!
        }
    }

   private fun getFileMimeType(mPath: String): String? {
        val path: Path = Paths.get(mPath)
        val mimeType: String?
        try {
            mimeType = Files.probeContentType(path)
        } catch (e: Exception) {
            Log.e("Get File", "Erro: $e")
            return null
        }
        return mimeType
    }

    fun getVideoDuration(videoPath: String): String {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)

        val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val durationInMillis = durationString?.toLongOrNull() ?: 0

        retriever.release()

        val hours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    @SuppressLint("SuspiciousIndentation")
    fun getImageProperties(fileItem: FileModel, mediaType: MidiaType): Fragment{
        val bitmap: Bitmap = BitmapFactory.decodeFile(fileItem.filePath)
        val width = bitmap.width
        val height = bitmap.height

        val dimension = "$width x $height"
        val format = bitmap.config

        val imageProperties = mutableListOf<FileProperties>()
        imageProperties.add(FileProperties("Item", "Icon", true, mediaType, fileItem.filePath))
        imageProperties.add(FileProperties("Dimensões", dimension))
        imageProperties.add(FileProperties("Formato da Imagen", format.toString()))

        addTabTitle("Imagem")
        return BasicPropertiesFragment.newInstance("null", "null", imageProperties)
    }
    fun getVideoProperties(fileItem: FileModel, mediaType: MidiaType): Fragment{
        val bitmap: Bitmap = BitmapFactory.decodeFile(fileItem.filePath)
        val width = bitmap.width
        val height = bitmap.height

        val dimension = "$width x $height"
       val format = bitmap.config

        val videoProperties = mutableListOf<FileProperties>()

        videoProperties.add(FileProperties("Item", "Icon", true, mediaType, fileItem.filePath))
        videoProperties.add(FileProperties("Dimensões", dimension))
        videoProperties.add(FileProperties("Formato do video", format.toString()))
        videoProperties.add(FileProperties("Duração", getVideoDuration(fileItem.filePath)))

        addTabTitle("Video")

        return BasicPropertiesFragment.newInstance("null", "null", videoProperties)
    }

    fun getBasicProperties(fileItem: FileModel): Fragment{
        addTabTitle("Básico")
        val mFile = File(fileItem.filePath)
        val fSize = FileUtils.getInstance().getFileSizeFormatted(mFile.length())
        val fileSize: String
        val fileTitle: String
        if (fileItem.isDirectory){

            val items = mFile.list()?.size
            fileTitle = "Conteúdo"
            fileSize = "$items itens"
        } else{
            fileTitle = "Tamanho"
            fileSize = fSize
        }

        val basicProperties = mutableListOf<FileProperties>()
        basicProperties.add(FileProperties("Nome", fileItem.fileName))
        basicProperties.add(FileProperties("Caminho", fileItem.filePath))
        basicProperties.add(FileProperties("Tipo", getFileMimeType(fileItem.filePath).toString()))
        basicProperties.add(FileProperties(fileTitle, fileSize))
        basicProperties.add(FileProperties("Última Modificação", FileUtils.getInstance().getFormatDateFile(fileItem.filePath, false)))

        addTabTitle("Básico")
        return BasicPropertiesFragment.newInstance("null", "null", basicProperties)
    }

    private fun String.isMimeTypeMedia(): Boolean {
        val mediaMimeTypes = listOf("video/", "audio/", "image/", "apk")
        val mimeType = this.lowercase(Locale.getDefault())
        return mediaMimeTypes.any { mimeType.startsWith(it) }
    }
}

class PropertiesFragment(){

    fun getFragmentPropriedades(position: Int, fileItem: FileModel): Fragment? {
        return when (position) {
            0 -> FilePropertiesUtil().getBasicProperties(fileItem)
            1 -> FilePropertiesUtil().getFileProperties(fileItem)
            else -> null
        }
    }
}

