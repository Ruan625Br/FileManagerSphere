package com.etb.filemanager.files.provider.archive.common

import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


fun writeFileInBytes(contentInBytes: ByteArray, outputPath: String) {
    try {
        val fout = FileOutputStream(outputPath)
        fout.write(contentInBytes)
        if (fout != null) {
            fout.close()
        }
    } catch (e: FileNotFoundException) {
        Log.e("ERRO AO ESCREVER", "Erro: $e")
    } catch (e: IOException) {
        Log.e("ERRO AO ESCREVER 1", "Erro: $e")

    }
}

fun readFile(file: File?): ByteArray? {
    var fileContent: ByteArray? = null
    var bis: BufferedInputStream? = null
    try {
        bis = BufferedInputStream(FileInputStream(file))
        val fileSize = bis.available()
        fileContent = ByteArray(fileSize)
        bis.read(fileContent)
        if (bis != null) {
            bis.close()
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return fileContent
}

fun zipMultipleFile(files: Array<File>): ByteArray? {
    val filesInZip: ByteArray? = null
    val byteOutStream = ByteArrayOutputStream()
    val zipOutStream = ZipOutputStream(byteOutStream)

    for (file in files) {
        val fileContentInBytes = readFile(file)
        if (fileContentInBytes != null && fileContentInBytes.isNotEmpty()) {
            val eachFileName = file.name
            val zipEntry = ZipEntry(eachFileName)
            try {
                zipOutStream.putNextEntry(zipEntry)
                zipOutStream.write(
                    fileContentInBytes, 0, fileContentInBytes.size
                )
                zipOutStream.closeEntry()
            } catch (e: IOException) {
                Log.e("ERRO AO COMPACTAR", "Erro: $e")

            }
        }
    }
    try {
        zipOutStream.finish()
    } catch (e: IOException) {
        Log.e("ERRO AO COMPACTAR", "Erro: $e")

    }
    try {
        zipOutStream.close()
    } catch (e: IOException) {
        Log.e("ERRO AO COMPACTAR", "Erro: $e")

    }

    return filesInZip
}