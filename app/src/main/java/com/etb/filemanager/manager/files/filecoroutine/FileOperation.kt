package com.etb.filemanager.manager.files.filecoroutine

import android.content.Context
import android.util.Log
import com.etb.filemanager.R
import com.etb.filemanager.files.util.ContextUtils
import com.etb.filemanager.manager.files.filecoroutine.CompressionType.*
import com.etb.filemanager.manager.util.MaterialDialogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.apache.commons.io.FileUtils
import java.io.*
import java.nio.file.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
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
        paths: List<String>, outputFilePath: String, compressionType: CompressionType, progressListener: (Int) -> Unit
    ) {
        val outputFile = File(outputFilePath)
        val outputStream = FileOutputStream(outputFile)

        outputStream.use {
            when (compressionType) {
                ZIP -> compressToZip(paths, outputFilePath, progressListener)
                SEVENZ -> TODO()
                TAR -> TODO()
                GZIP -> TODO()
                XZ -> TODO()
                GZ -> TODO()
                ZSTD -> TODO()
                TARXZ -> TODO()
                TARGZ -> TODO()
                TARZSTD -> TODO()
            }
        }
    }

    private var totalFiles = 0
    private var processedFiles = 0

    private fun compressToZip(filesToArchive: List<String>, outputFilePath: String, progressListener: (Int) -> Unit) {
        val zipFile = File(outputFilePath)
        val zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))

        for (filePath in filesToArchive) {
            val file = File(filePath)
            if (file.exists()) {
                if (file.isDirectory) {
                    addDirectoryToZip(file, "", zipOutputStream, progressListener)
                } else {
                    addFileToZip(file, "", zipOutputStream, progressListener)
                }
            }
        }

        zipOutputStream.close()
    }

    private fun addDirectoryToZip(
        directory: File, parentPath: String, zipOutputStream: ZipOutputStream, progressListener: (Int) -> Unit
    ) {
        val files = directory.listFiles()
        val buffer = ByteArray(1024)

        for (file in files!!) {
            totalFiles++
            val relativePath = if (parentPath.isNotEmpty()) "$parentPath/${file.name}" else file.name

            if (file.isDirectory) {
                addDirectoryToZip(file, relativePath, zipOutputStream, progressListener)
            } else {
                val inputStream = FileInputStream(file)
                zipOutputStream.putNextEntry(ZipEntry(relativePath))

                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    zipOutputStream.write(buffer, 0, length)
                }

                zipOutputStream.closeEntry()
                inputStream.close()
            }

            processedFiles++
            val progress = (processedFiles.toDouble() / totalFiles.toDouble()) * 100
            progressListener(progress.toInt())
        }
    }

    private fun addFileToZip(
        file: File, parentPath: String, zipOutputStream: ZipOutputStream, progressListener: (Int) -> Unit
    ) {
        val buffer = ByteArray(1024)
        val inputStream = FileInputStream(file)
        zipOutputStream.putNextEntry(ZipEntry("$parentPath/${file.name}"))

        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            zipOutputStream.write(buffer, 0, length)
        }

        zipOutputStream.closeEntry()
        inputStream.close()

        processedFiles++
        val progress = (processedFiles.toDouble() / totalFiles.toDouble()) * 100
        progressListener(progress.toInt())
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
        val zipInputStream = ArchiveStreamFactory().createArchiveInputStream("zip", FileInputStream(archiveFile))
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
    ZIP, SEVENZ, TAR, GZIP, XZ, GZ, ZSTD, TARXZ, TARGZ, TARZSTD,
}

enum class FileOperation {
    DELETE, CREATE, RENAME, MOVE, COPY, COMPRESS, EXTRACT
}