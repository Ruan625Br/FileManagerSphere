package com.etb.filemanager.files.file.properties

import android.util.Log
import androidx.fragment.app.Fragment
import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.manager.util.FileUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FilePropertiesUtil {

    private val fragmentProperties = mutableListOf<Fragment>();
    fun addFragmentProperties(fragment: Fragment){
        fragmentProperties.add(fragment)
    }

    fun getFragmentProperties(position: Int): Fragment{
        return fragmentProperties[position]
    }


    fun getFileMimeType(mPath: String): String? {
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
        val mFile: File = File(fileItem.filePath)
        var fSize = FileUtils.getInstance().getFileSize(mFile)
        var fileSize: String
        var fileTitle: String
        if (fileItem.isDirectory){

            val items = mFile.list().size
            fileTitle = "Conteúdo"
            fileSize = "$items itens, totalizando $fSize"
        } else{
            fileTitle = "Tamanho"
            fileSize = fSize.toString()
        }

        val basicProperties = mutableListOf<FileProperties>()
        basicProperties.add(FileProperties("Nome", fileItem.fileName))
        basicProperties.add(FileProperties("Caminho", fileItem.filePath))
        basicProperties.add(FileProperties("Tipo", getFileMimeType(fileItem.filePath).toString()))
        basicProperties.add(FileProperties(fileTitle, fileTitle))
        basicProperties.add(FileProperties("Última Modificaçãõ", FileUtils.getInstance().getFormatDateFile(fileItem.filePath, false)))

        return basicProperties
    }
}