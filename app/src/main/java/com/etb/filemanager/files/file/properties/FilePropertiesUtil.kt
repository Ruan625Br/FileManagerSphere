package com.etb.filemanager.files.file.properties

import android.util.Log
import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.manager.util.FileUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FilePropertiesUtil {

    private val listTabTitle: MutableList<String> = mutableListOf()

    fun getTabTitle(position: Int): String {
        if (listTabTitle != null){

            return listTabTitle[position]


        }
        return "null"
    }

    private fun addTabTitle(title: String) {
        listTabTitle.add(title)
    }


    fun getFileProperties(fileItem: FileModel){
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
    fun getBasicProperties(fileItem: FileModel): MutableList<FileProperties>{
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

        return basicProperties
    }
}