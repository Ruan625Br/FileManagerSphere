/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FileOperation.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.files.filecoroutine

import android.content.Context
import android.util.Log
import com.etb.filemanager.R
import com.etb.filemanager.files.util.ContextUtils
import com.etb.filemanager.manager.files.filecoroutine.CompressionType.*
import com.etb.filemanager.manager.util.MaterialDialogUtils
import kala.compress.archivers.zip.ZipArchiveEntry
import kala.compress.archivers.zip.ZipArchiveOutputStream
import kala.compress.compressors.gzip.GzipCompressorOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream
import org.apache.commons.io.FileUtils
import java.io.*
import java.nio.file.*
import kotlin.io.path.*


suspend fun performFileOperation(
    context: Context,
    operation: FileOperation,
    sourcePath: List<String>?,
    newNames: List<String>?,
    createDir: Boolean?,
    destinationPath: String?,
    compressionType: CompressionType?,
    progressListener: (Int) -> Unit,
    completionListener: (Boolean) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {

            when (operation) {
                FileOperation.DELETE -> deleteFile(sourcePath!!, progressListener)
                FileOperation.CREATE -> create(sourcePath!!, createDir!!, progressListener)
                FileOperation.RENAME -> rename(sourcePath!!, newNames!!, progressListener)
                FileOperation.MOVE -> move(sourcePath!!, destinationPath!!, progressListener)
                FileOperation.COPY -> copy(sourcePath!!, destinationPath!!, progressListener)
                FileOperation.COMPRESS -> CompressFiles().compressFiles(
                    sourcePath!!, destinationPath!!, compressionType!!, progressListener
                )

                FileOperation.EXTRACT -> {
                    ExtractArchives().extractFiles(sourcePath!!, destinationPath!!)
                }
            }
            completionListener(true)
        } catch (e: Exception) {
            completionListener(false)
        }
    }
}

@OptIn(ExperimentalPathApi::class)
private fun deleteFile(
    paths: List<String>, progressListener: (Int) -> Unit
) {
    var completedFiles = 0
    val totalFiles = paths.size

    for (path in paths) {
        val mPath = Paths.get(path)
        try {
            mPath.deleteRecursively()
            completedFiles++
            val progress = (completedFiles * 100 / totalFiles).toInt()
            sendProgress(progressListener, progress)
        } catch (e: IOException) {
            try {
                showOperationDialog()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


private fun create(
    paths: List<String>, dir: Boolean, progressListener: (Int) -> Unit
) {
    var completedFiles = 0
    val totalFiles = paths.size

    for (path in paths) {
        val mPath = Paths.get(path)
        if (!mPath.exists()) {
            try {
                completedFiles++
                val progress = (completedFiles * 100 / totalFiles)
                sendProgress(progressListener, progress)
                if (dir) {
                    mPath.createDirectory()
                } else {
                    mPath.createFile()
                }
            } catch (e: Exception) {
                Log.e("Operation create", "Erro: $e")
            }
        } else {
            //TODO()
        }
    }
}

private fun rename(
    paths: List<String>, newNames: List<String>, progressListener: (Int) -> Unit
) {
    var completedFiles = 0
    val totalFiles = paths.size

    for ((index, mPath) in paths.withIndex()) {
        val path = Paths.get(mPath)
        val newPath = path.resolveSibling(newNames[index])
        moveAtomically(path, newPath)
        completedFiles++
        val progress = (completedFiles * 100 / totalFiles).toInt()
        sendProgress(progressListener, progress)
    }
}


private fun moveAtomically(source: Path, target: Path) {
    try {
        source.moveTo(target, LinkOption.NOFOLLOW_LINKS, StandardCopyOption.ATOMIC_MOVE)

    } catch (e: InterruptedIOException) {
        showOperationDialog()
    }
}

private fun move(paths: List<String>, destinationPath: String, progressListener: (Int) -> Unit) {
    try {
        var completedSize = 0L
        var totalSize = 0L

        for (path in paths) {
            val sourcePath = Paths.get(path)
            totalSize += Files.size(sourcePath)
        }
        for (path in paths) {
            val sourcePath = Paths.get(path)
            val destinationFolderPath = Paths.get(destinationPath)
            val destinationFilePath = destinationFolderPath.resolve(sourcePath.fileName)

            if (Files.exists(destinationFilePath)) {
                //TODO()
                //   val dialogResult = showConfirmationDialog(sourcePath.fileName.toString())
            }
            try {

                val fileSize = Files.size(sourcePath)
                completedSize += fileSize
                val progress = (completedSize * 100 / totalSize).toInt()
                sendProgress(progressListener, progress)
                Files.move(sourcePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING)


            } catch (e: IOException) {
                Log.e("FILEOP", "Erro: $e")
            }
        }
    } catch (e: InterruptedException) {
        Log.e("EROOOO", "Erro: $e")
    }
}

private fun copy(paths: List<String>, destinationPath: String, progressListener: (Int) -> Unit) {
    try {
        var completedSize = 0L
        var totalSize = 0L

        for (path in paths) {
            val sourcePath = Paths.get(path)
            totalSize += Files.size(sourcePath)
        }
        for (path in paths) {
            val sourcePath = Paths.get(path)
            val destinationFolderPath = Paths.get(destinationPath)
            val destinationFilePath = destinationFolderPath.resolve(sourcePath.fileName)

            if (Files.exists(destinationFilePath)) {
                //TODO()
                //   val dialogResult = showConfirmationDialog(sourcePath.fileName.toString())
            }
            try {

                val fileSize = Files.size(sourcePath)
                completedSize += fileSize
                val progress = (completedSize * 100 / totalSize).toInt()
                sendProgress(progressListener, progress)
                Files.copy(sourcePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING)


            } catch (e: IOException) {
                Log.e("FILEOP", "Erro: $e")
            }
        }
    } catch (e: InterruptedException) {
        Log.e("EROOOO", "Erro: $e")
    }
}

private fun showOperationDialog() {
    val mContext = ContextUtils.getContext()
    val title = mContext.getString(R.string.error_occurred)
    val message = mContext.getString(R.string.error_occurred_during_operation)
    val textPositiveButton = mContext.getString(R.string.ok)

    MaterialDialogUtils().createDialogInfo(
        title, message, textPositiveButton, "", mContext, false
    ) { dialogResult ->
        val isConfirmed = dialogResult.confirmed
        if (isConfirmed) {

        }
    }
}

private fun showConfirmationDialog(fileName: String): Boolean {
    var isConfirmed = false
    val context = ContextUtils.getContext()
    val title = context.getString(R.string.warning)
    val message = "O arquivo '$fileName' já existe. Deseja substituí-lo?"
    val textPositiveButton = context.getString(R.string.ok)
    val textNegativeButton = context.getString(R.string.skip)

    MaterialDialogUtils().createDialogInfo(
        title, message, textPositiveButton, textNegativeButton, context, true
    ) { dialogResult ->
        isConfirmed = dialogResult.confirmed
    }
    return isConfirmed
}


private fun sendProgress(progressListener: (Int) -> Unit, progress: Int) {
    progressListener(progress)
}


class CompressFiles {

    fun compressFiles(
        paths: List<String>,
        outputFilePath: String,
        compressionType: CompressionType,
        progressListener: (Int) -> Unit
    ) {
        val outputFile = File(outputFilePath)
        val outputStream = FileOutputStream(outputFile)

        outputStream.use {
            when (compressionType) {
                ZIP -> compressToZip(paths, outputFilePath, progressListener)
                SEVENZ -> compressToSevenZ(paths, outputFilePath, progressListener)
                TAR -> compressToTar(paths, outputFilePath, progressListener)
                TARXZ -> compressToXz(paths, outputFilePath, progressListener)
                TARGZ -> compressToGzip(paths, outputFilePath, progressListener)
                TARZSTD -> TODO()
            }
        }
    }

    private fun compressToZip(
        filesToArchive: List<String>,
        outputFilePath: String,
        progressListener: (Int) -> Unit
    ) {
        val outputZipFile = File(outputFilePath)
        val outputStream = FileOutputStream(outputZipFile)
        val zipOutputStream = ZipArchiveOutputStream(outputStream)

        try {
            val totalSize = filesToArchive.sumOf { File(it).length() }

            for (filePath in filesToArchive) {
                val sourceFile = File(filePath)
                addFileToZip(zipOutputStream, sourceFile, "", totalSize, progressListener)
            }
        } catch (e: Exception) {
            // Trate as exceções adequadamente
        } finally {
            zipOutputStream.close()
            outputStream.close()
        }
    }

    private fun addFileToZip(
        zipOutputStream: ZipArchiveOutputStream,
        file: File,
        entryPath: String,
        totalSize: Long,
        progressListener: (Int) -> Unit
    ) {
        val entryName = if (entryPath.isNotEmpty()) "$entryPath/${file.name}" else file.name
        val zipEntry = ZipArchiveEntry(entryName)

        if (file.isFile) {
            zipOutputStream.putArchiveEntry(zipEntry)

            val buffer = ByteArray(1024)
            val inputStream = FileInputStream(file)
            var bytesRead: Int
            var totalBytesRead = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                zipOutputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                val progress = ((totalBytesRead * 100) / totalSize).toInt()
                progressListener(progress)
            }

            inputStream.close()
            zipOutputStream.closeArchiveEntry()
        } else if (file.isDirectory) {
            val children = file.listFiles()
            if (children != null && children.isNotEmpty()) {
                for (childFile in children) {
                    addFileToZip(zipOutputStream, childFile, entryName, totalSize, progressListener)
                }
            } else {
                zipOutputStream.putArchiveEntry(zipEntry)
                zipOutputStream.closeArchiveEntry()
            }
        }
    }

    private fun compressToSevenZ(
        filesToArchive: List<String>,
        outputFilePath: String,
        progressListener: (Int) -> Unit
    ) {
        val outputSevenZFile = File(outputFilePath)
        val outputStream = FileOutputStream(outputSevenZFile)
        val sevenZOutputFile = SevenZOutputFile(outputStream.channel)

        try {
            val totalSize = filesToArchive.sumOf { File(it).length() }

            for (filePath in filesToArchive) {
                val sourceFile = File(filePath)
                addFileToSevenZ(sevenZOutputFile, sourceFile, "", totalSize, progressListener)
            }
        } catch (e: Exception) {
            // Handle exceptions appropriately
        } finally {
            sevenZOutputFile.close()
            outputStream.close()
        }
    }

    private fun addFileToSevenZ(
        sevenZOutputFile: SevenZOutputFile,
        file: File,
        entryPath: String,
        totalSize: Long,
        progressListener: (Int) -> Unit
    ) {
        val entryName = if (entryPath.isNotEmpty()) "$entryPath/${file.name}" else file.name

        if (file.isFile) {
            val entry = sevenZOutputFile.createArchiveEntry(file, entryName)
            sevenZOutputFile.putArchiveEntry(entry)

            val buffer = ByteArray(1024)
            val inputStream = FileInputStream(file)
            var bytesRead: Int
            var totalBytesRead = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                sevenZOutputFile.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                val progress = ((totalBytesRead * 100) / totalSize).toInt()
                progressListener(progress)
            }

            inputStream.close()
            sevenZOutputFile.closeArchiveEntry()
        } else if (file.isDirectory) {
            val children = file.listFiles()
            if (children != null && children.isNotEmpty()) {
                for (childFile in children) {
                    addFileToSevenZ(
                        sevenZOutputFile,
                        childFile,
                        entryName,
                        totalSize,
                        progressListener
                    )
                }
            } else {
                val entry = sevenZOutputFile.createArchiveEntry(file, entryName + "/")
                sevenZOutputFile.putArchiveEntry(entry)
                sevenZOutputFile.closeArchiveEntry()
            }
        }
    }

    private fun compressToTar(
        filesToArchive: List<String>,
        outputFilePath: String,
        progressListener: (Int) -> Unit
    ) {
        val outputTarFile = File(outputFilePath)
        val outputStream = FileOutputStream(outputTarFile)
        val tarOutputStream = TarArchiveOutputStream(outputStream)

        try {
            val totalSize = calculateTotalSize(filesToArchive)

            for (filePath in filesToArchive) {
                val sourceFile = File(filePath)
                addFileToTar(tarOutputStream, sourceFile, "", totalSize, progressListener)
            }
        } catch (e: Exception) {
            // Handle exceptions appropriately
        } finally {
            tarOutputStream.close()
            outputStream.close()
        }
    }

    private fun addFileToTar(
        tarOutputStream: TarArchiveOutputStream,
        file: File,
        entryPath: String,
        totalSize: Long,
        progressListener: (Int) -> Unit
    ) {
        val entryName = if (entryPath.isNotEmpty()) "$entryPath/${file.name}" else file.name
        val tarEntry = TarArchiveEntry(file, entryName)

        if (file.isFile) {
            tarOutputStream.putArchiveEntry(tarEntry)

            val buffer = ByteArray(1024)
            val inputStream = FileInputStream(file)
            var bytesRead: Int
            var totalBytesRead = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                tarOutputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                val progress = ((totalBytesRead * 100) / totalSize).toInt()
                progressListener(progress)
            }

            inputStream.close()
            tarOutputStream.closeArchiveEntry()
        } else if (file.isDirectory) {
            val children = file.listFiles()
            if (children != null && children.isNotEmpty()) {
                for (childFile in children) {
                    addFileToTar(tarOutputStream, childFile, entryName, totalSize, progressListener)
                }
            } else {
                tarOutputStream.putArchiveEntry(tarEntry)
                tarOutputStream.closeArchiveEntry()
            }
        }
    }


    private fun compressToXz(
        filesToArchive: List<String>,
        outputFilePath: String,
        progressListener: (Int) -> Unit
    ) {
        val outputXzFile = File(outputFilePath)
        val outputStream = FileOutputStream(outputXzFile)
        val xzOutputStream = XZCompressorOutputStream(outputStream)
        val tarArchiveOutputStream = TarArchiveOutputStream(xzOutputStream)

        try {
            val totalSize = calculateTotalSize(filesToArchive)

            for (filePath in filesToArchive) {
                val sourceFile = File(filePath)
                addFileToTar(tarArchiveOutputStream, sourceFile, "", totalSize, progressListener)
            }
        } catch (e: Exception) {
            // Handle exceptions appropriately
        } finally {
            tarArchiveOutputStream.finish()
            tarArchiveOutputStream.close()
            xzOutputStream.close()
            outputStream.close()
        }
    }

    private fun compressToGzip(
        filesToArchive: List<String>,
        outputFilePath: String,
        progressListener: (Int) -> Unit
    ) {
        val outputGzipFile = File(outputFilePath)
        val outputStream = FileOutputStream(outputGzipFile)
        val gzipOutputStream = GzipCompressorOutputStream(outputStream)
        val tarArchiveOutputStream = TarArchiveOutputStream(gzipOutputStream)

        try {
            val totalSize = calculateTotalSize(filesToArchive)

            for (filePath in filesToArchive) {
                val sourceFile = File(filePath)
                addFileToTar(tarArchiveOutputStream, sourceFile, "", totalSize, progressListener)
            }
        } catch (e: Exception) {
            // Handle exceptions appropriately
        } finally {
            tarArchiveOutputStream.finish()
            tarArchiveOutputStream.close()
            gzipOutputStream.close()
            outputStream.close()
        }
    }


    private fun calculateTotalSize(filesToArchive: List<String>): Long {
        var totalSize = 0L
        for (filePath in filesToArchive) {
            val file = File(filePath)
            totalSize += if (file.isDirectory) {
                file.walk().map { it.length() }.sum()
            } else {
                file.length()
            }
        }
        return totalSize
    }

}

class ExtractArchives {


    fun extractFiles(paths: List<String>, outputDirectory: String) {
        val outputDirFile = File(outputDirectory)

        paths.forEach { archivePath ->
            val archiveFile = File(archivePath)

            if (!archiveFile.exists() || !outputDirFile.isDirectory) {
                throw IllegalArgumentException("Invalid archive or output directory.")
            }

            val extension = getExtension(archiveFile.name)

            when (extension.toLowerCase()) {
                "zip" -> extractZipArchive(archiveFile, outputDirFile)
                "7z" -> extract7zArchive(archiveFile, outputDirFile)
                "tar", "gz", "xz" -> {}
                else -> throw IllegalArgumentException("Unsupported archive format.")
            }
        }
    }

    private fun extractZipArchive(archiveFile: File, outputDirFile: File) {
        val zipInputStream =
            ArchiveStreamFactory().createArchiveInputStream("zip", FileInputStream(archiveFile))
        var entry: ArchiveEntry? = zipInputStream.nextEntry

        while (entry != null) {
            if (!entry.isDirectory) {
                val outputFilePath = File(outputDirFile, entry.name)
                FileUtils.copyInputStreamToFile(zipInputStream, outputFilePath)
            }

            entry = zipInputStream.nextEntry
        }

        zipInputStream.close()
    }


    private fun extract7zArchive(archiveFile: File, outputDirFile: File) {
        try {
            val sevenZFile = SevenZFile(archiveFile)
            var entry: SevenZArchiveEntry? = sevenZFile.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    val outputFilePath = File(outputDirFile, entry.name)
                    val outputFile = outputFilePath.absoluteFile
                    if (!outputFile.parentFile!!.exists()) {
                        outputFile.parentFile?.mkdirs()
                    }
                    val outputStream = FileOutputStream(outputFile)
                    val content = ByteArray(entry.size.toInt())
                    sevenZFile.read(content, 0, content.size)
                    outputStream.write(content)
                    outputStream.close()
                }
                entry = sevenZFile.nextEntry
            }
            sevenZFile.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun getExtension(filename: String): String {
        val dotIndex = filename.lastIndexOf(".")
        return if (dotIndex > 0 && dotIndex < filename.length - 1) {
            filename.substring(dotIndex + 1)
        } else {
            ""
        }
    }

}


enum class CompressionType {
    ZIP, SEVENZ, TAR, TARXZ, TARGZ, TARZSTD,
}

enum class FileOperation {
    DELETE, CREATE, RENAME, MOVE, COPY, COMPRESS, EXTRACT
}