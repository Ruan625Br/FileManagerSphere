/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ArchiveWriter.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

import android.annotation.SuppressLint
import android.os.Build
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveException
import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarConstants
import org.apache.commons.compress.archivers.zip.UnixStat
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.compressors.CompressorException
import org.apache.commons.compress.compressors.CompressorStreamFactory
import java.io.*
import java.nio.channels.Channels
import java.nio.channels.SeekableByteChannel
import java.nio.channels.WritableByteChannel
import java.nio.file.LinkOption
import java.nio.file.Path

class ArchiveWriter(private val archiveType: String, private val compressorType: String?, private val channel: SeekableByteChannel) : Closeable {
    private val archiveOutputStream: ArchiveOutputStream

    init {
        archiveOutputStream = createArchiveOutputStream()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createArchiveOutputStream(): ArchiveOutputStream {
        return when (archiveType) {
            ArchiveStreamFactory.SEVEN_Z -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    throw UnsupportedOperationException("SevenZOutputFile is not supported on this Android version.")
                }
                val sevenZOutputFile = SevenZOutputFile(channel)
                SevenZArchiveOutputStream(sevenZOutputFile)
            }
            else -> {
                val outputStream = Channels.newOutputStream(channel).buffered()
                val compressorOutputStream = compressorType?.let {
                    CompressorStreamFactory().createCompressorOutputStream(it, outputStream)
                } ?: outputStream
                ArchiveStreamFactory().createArchiveOutputStream(archiveType, compressorOutputStream)
            }
        }
    }

    @Throws(IOException::class)
    fun writeFile(file: Path, entryName: String) {
        val entry = createArchiveEntry(file, entryName)
        archiveOutputStream.putArchiveEntry(entry)
        FileInputStream(file.toFile()).use { input ->
            input.copyTo(archiveOutputStream)
        }
        archiveOutputStream.closeArchiveEntry()
    }

    @Throws(IOException::class)
    override fun close() {
        archiveOutputStream.finish()
        archiveOutputStream.close()
    }

    private fun createArchiveEntry(file: Path, entryName: String): ArchiveEntry {
        return when (archiveType) {
            ArchiveStreamFactory.ZIP -> {
                val entry = ZipArchiveEntry(file.toFile(), entryName)
                entry.unixMode = UnixStat.DEFAULT_FILE_PERM
                entry
            }
            ArchiveStreamFactory.TAR -> {
                val entry = TarArchiveEntry(file.toFile(), entryName)
                entry.mode = UnixStat.DEFAULT_FILE_PERM
                entry
            }
            else -> throw ArchiveException("Unsupported archive type: $archiveType")
        }
    }

    private inner class SevenZArchiveOutputStream(private val file: SevenZOutputFile) : ArchiveOutputStream() {
        override fun createArchiveEntry(file: File, entryName: String): ArchiveEntry {
            return file.let { this.file.createArchiveEntry(it, entryName) }
        }

        override fun putArchiveEntry(entry: ArchiveEntry) {
            file.putArchiveEntry(entry)
        }

        override fun write(b: Int) {
            file.write(b)
        }

        override fun write(b: ByteArray) {
            file.write(b)
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            file.write(b, off, len)
        }

        override fun closeArchiveEntry() {
            file.closeArchiveEntry()
        }

        override fun finish() {
            file.finish()
        }

        override fun close() {
            file.close()
        }
    }
}

fun SeekableByteChannel.newArchiveWriter(archiveType: String, compressorType: String? = null): ArchiveWriter {
    return ArchiveWriter(archiveType, compressorType, this)
}

fun main() {
    val outputFilePath = "/path/to/output/archive.zip"
    val filesToArchive = listOf("/path/to/file1.txt", "/path/to/file2.txt")

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