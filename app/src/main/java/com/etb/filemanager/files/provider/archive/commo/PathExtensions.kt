package com.etb.filemanager.files.provider.archive.commo


import java.io.IOException
import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.spi.FileSystemProvider


@Throws(IOException::class)
fun Path.newDirectoryStream(): DirectoryStream<Path> = Files.newDirectoryStream(this)

val Path.provider: FileSystemProvider
    get() = fileSystem.provider()
