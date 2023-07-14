package com.etb.filemanager.manager.files.filecoroutine

import android.content.Context
import android.util.Log
import com.etb.filemanager.R
import com.etb.filemanager.files.util.ContextUtils
import com.etb.filemanager.manager.files.filecoroutine.CompressionType.*
import com.etb.filemanager.manager.util.MaterialDialogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import newArchiveWriter
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream
import java.io.*
import java.nio.channels.FileChannel
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
                FileOperation.COMPRESS -> compressFiles(sourcePath!!, destinationPath!!, compressionType!!)
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

private fun compressFiles(paths: List<String>, outputFilePath: String, compressionType: CompressionType) {
    val outputFile = File(outputFilePath)
    val outputStream = FileOutputStream(outputFile)

    outputStream.use {
        when (compressionType) {
            ZIP -> compressToZip(paths, outputFilePath)
            TAR -> compressToTar(paths, outputFilePath)
            GZIP -> compressToGzip(paths, outputFilePath)
            XZ -> compressToXz(paths, outputFilePath)
            ZSTANDARD -> compressToZstandard(paths, outputFilePath)
            SEVENZ -> compressToSevenZ(paths, outputFilePath)
        }
    }
}

private fun compressToZip(filesToArchive: List<String>, outputFilePath: String) {

    val outputChannel = File(outputFilePath).outputStream().channel
    val archiveWriter = outputChannel.newArchiveWriter(ArchiveStreamFactory.ZIP)

    for (filePath in filesToArchive) {
        val file = File(filePath)
        val entryName = file.name
        archiveWriter.writeFile(file.toPath(), entryName)
    }

    archiveWriter.close()
    outputChannel.close()


}

private fun compressToTar(filesToArchive: List<String>, outputFilePath: String) {
    val outputChannel = File(outputFilePath).outputStream().channel
    val archiveWriter = outputChannel.newArchiveWriter(ArchiveStreamFactory.TAR)

    for (filePath in filesToArchive) {
        val file = File(filePath)
        val entryName = file.name
        archiveWriter.writeFile(file.toPath(), entryName)
    }

    archiveWriter.close()
    outputChannel.close()
}

private fun compressToGzip(filesToArchive: List<String>, outputFilePath: String) {
    val outputChannel = FileChannel.open(File(outputFilePath).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    val compressionType = CompressorStreamFactory.GZIP
    val archiveType = ArchiveStreamFactory.ZIP
    val archiveWriter = outputChannel.newArchiveWriter(archiveType, compressionType)

    for (filePath in filesToArchive) {
        val file = File(filePath)
        val entryName = file.name
        archiveWriter.writeFile(file.toPath(), entryName)
    }

    archiveWriter.close()
    outputChannel.close()
}

private fun compressToXz(filesToArchive: List<String>, outputFilePath: String) {
    val outputChannel = FileChannel.open(File(outputFilePath).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)
   val compressionType = CompressorStreamFactory.XZ
    val archiveType = ArchiveStreamFactory.TAR
    val archiveWriter = outputChannel.newArchiveWriter(archiveType, compressionType)

    for (filePath in filesToArchive) {
        val file = File(filePath)
        val entryName = file.name
        archiveWriter.writeFile(file.toPath(), entryName)
    }

    archiveWriter.close()
    outputChannel.close()
}

private fun compressToSevenZ(filesToArchive: List<String>, outputFilePath: String) {
    val outputChannel = File(outputFilePath).outputStream().channel
    val archiveWriter = outputChannel.newArchiveWriter(ArchiveStreamFactory.SEVEN_Z)

    for (filePath in filesToArchive) {
        val file = File(filePath)
        val entryName = file.name
        archiveWriter.writeFile(file.toPath(), entryName)
    }

    archiveWriter.close()
    outputChannel.close()
}

private fun compressToZstandard(filesToArchive: List<String>, outputFilePath: String) {
    val outputChannel = FileChannel.open(File(outputFilePath).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    val compressionType = CompressorStreamFactory.ZSTANDARD
    val archiveType = ArchiveStreamFactory.TAR
    val archiveWriter = outputChannel.newArchiveWriter(archiveType, compressionType)

    for (filePath in filesToArchive) {
        val file = File(filePath)
        val entryName = file.name
        archiveWriter.writeFile(file.toPath(), entryName)
    }

    archiveWriter.close()
    outputChannel.close()
}


enum class CompressionType {
    ZIP, TAR, GZIP, XZ, SEVENZ, ZSTANDARD
}

enum class FileOperation {
    DELETE, CREATE, RENAME, MOVE, COPY, COMPRESS
}