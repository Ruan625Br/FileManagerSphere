/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FileUtil.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.util

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.etb.filemanager.BuildConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
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

    fun copyTextToClipboard(context: Context, text: String, toast: Boolean) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", text)
        clipboardManager.setPrimaryClip(clipData)

        if (toast) {
            Toast.makeText(context, "\"$text\" Caminho Copiado", Toast.LENGTH_LONG).show()
        }
    }

    fun shareFile(path: String, context: Context) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(context,  BuildConfig.FILE_PROVIDER_AUTHORITY, file)
        val mimeType = getMimeType(uri, null)


        val intent = Intent(Intent.ACTION_SEND)
        intent.type = if (file.isDirectory) "vnd.android.document/directory" else mimeType
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(intent)
    }

    fun shareFiles(filePaths: List<String>, context: Context) {
        //val context = ContextUtils.getContext()
        val files = ArrayList<File>()

        for (filePath in filePaths) {
            val file = File(filePath)
            if (file.exists()) {
                files.add(file)
            }
        }

        if (files.isNotEmpty()) {
            val uris = ArrayList<Uri>()
            for (file in files) {
                val uri = FileProvider.getUriForFile(context, BuildConfig.FILE_PROVIDER_AUTHORITY, file)
                uris.add(uri)
            }

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "*/*"
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(Intent.createChooser(intent, "Compartilhar arquivos"))
        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    fun actionOpenWith(path: String, context: Context) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        val mimeType = getMimeType(uri, null)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }


    fun getMimeType(uri: Uri?, mPath: String?): String? {
        val path = if (uri == null) mPath else uri.path
        val file = File(path!!)
        if (file.isDirectory) return "vnd.android.document/directory"
        val lastDotIndex = path.lastIndexOf(".")
        if (lastDotIndex != -1) {
            val extension = path.substring(lastDotIndex + 1)
            val mimeTypeMap = MimeTypeMap.getSingleton()
            return mimeTypeMap.getMimeTypeFromExtension(extension.lowercase())
        }
        return null
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
            } else {
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

fun jsonStringToList(jsonString: String?): List<String> {
    if (jsonString.isNullOrEmpty()) {
        return emptyList()
    }
    return Gson().fromJson(jsonString, object : TypeToken<List<String>>() {}.type)
}

fun stringListToJsonString(stringList: List<String>): String {
    return Gson().toJson(stringList)
}
