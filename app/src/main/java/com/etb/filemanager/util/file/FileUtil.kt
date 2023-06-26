package com.etb.filemanager.util.file

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


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


    fun renameFile(path: String, newFileName: String) {
        val sourcePath = Paths.get(path)
        val targetPath = sourcePath.resolveSibling(newFileName)

        try {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
        } catch (e: Exception) {
           Log.e("Erro ao renomear", "Erro: ${e.message}")
           Log.e("File", "sourcePath: $sourcePath\ntargetPath: $targetPath\nnewFileName: $newFileName")
        }
    }

    fun copyTextToClipboard(context: Context, text: String, toast: Boolean) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", text)
        clipboardManager.setPrimaryClip(clipData)

        if (toast){
            Toast.makeText(context, "\"$text\" Caminho Copiado", Toast.LENGTH_LONG).show()
        }
    }

    fun shareFile(path: String, context: Context){
        val file = File(path)
        val mimeType = getFileMimeType(path)
        val uri = FileProvider.getUriForFile(context, "com.etb.filemanager.fileprovider", file)

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = if (file.isDirectory) "vnd.android.document/directory" else mimeType
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(intent)
    }
    fun actionOpenWith(path: String, context: Context) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(context, "com.etb.filemanager.fileprovider", file)
        val mimeType = getMimeType(uri, context)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = if (file.isDirectory) "vnd.android.document/directory" else mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)
    }



    fun getMimeType(uri: Uri,context: Context): String? {
        val cRes = context.contentResolver

        return cRes.getType(uri)
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

    fun getFilePathFromUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                filePath = it.getString(columnIndex)
            }
        }

        return filePath
    }

    fun readFileAsString(filePath: String?): String {
        val stringBuilder = StringBuilder()
        try {
            val file = filePath?.let { File(it) }
            if (file != null && file.length() > 0) {
                val fis = FileInputStream(file)
                val isr = InputStreamReader(fis)
                val bufferedReader = BufferedReader(isr)
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }
                bufferedReader.close()
                isr.close()
                fis.close()
            } else{
                return ""
            }
        } catch (e: IOException) {
            Log.e("Erro ao ler arquivo", "Erro: " + e.message)
            return "Ocorreu um erro ao ler o arquivo."
        }
        return stringBuilder.toString()
    }

    fun saveFile(filePath: String, content: String): Boolean {
        return try {
            val writer = FileWriter(filePath)
            writer.write(content)
            writer.close()
            true
        } catch (e: IOException) {
            Log.i("Save file", "Erro: ${e.message}")
            false
        }
    }



    enum class TypeFile() {
        FILE, FOLDER
    }
}