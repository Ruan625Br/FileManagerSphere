package com.etb.filemanager.util.file

import android.util.Log
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class FileUtil {


    fun getFileAndFolderName(typeFile: TypeFile, path: String): String {
        return when (typeFile) {
            TypeFile.FILE -> getFolderName(path)
            TypeFile.FOLDER -> getFolderName(path)
        }

    }

    private fun getFolderName(pathString: String): String {
        val path: Path = Paths.get(pathString)
        return path.fileName.toString()
    }


    fun getFileExtension(path: Path): String {
        val fileName = path.fileName.toString()
        val dotIndex = fileName.lastIndexOf(".")
        return if (dotIndex > 0 && dotIndex < fileName.length - 1) {
            fileName.substring(dotIndex + 1).toLowerCase()
        } else {
            ""
        }
    }

    fun getFileSize(path: Path): Long {
        return Files.size(path)
    }

    fun createFolder(path: String, pathName: String): Boolean {
        val folderPath = Paths.get("$path/$pathName")

        return try {
            Files.createDirectory(folderPath)
            true
        } catch (e: IOException) {
            Log.e("Erro ao criar a pasta", "Erro: $e")
            false

        }
    }

    fun isValidName( name: String): Boolean {
        return  isValidNameFolder(name)

    }

    private fun isValidNameFolder(folderName: String): Boolean {
        val regex = Regex("^[^/\\\\?%*:|\"<>]*$") // Padrão regex para validar o nome da pasta
        return regex.matches(folderName)

    }





    enum class TypeFile() {
        FILE, FOLDER
    }
}