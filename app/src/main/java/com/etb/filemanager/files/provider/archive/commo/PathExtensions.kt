package com.etb.filemanager.files.provider.archive.commo


import java.io.IOException
import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.spi.FileSystemProvider
import kotlin.io.path.isDirectory


@Throws(IOException::class)
fun Path.newDirectoryStream(): DirectoryStream<Path> = Files.newDirectoryStream(this)

val Path.provider: FileSystemProvider
    get() = fileSystem.provider()


fun AutoCloseable.closeSafe() {
    try {
        close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Throws(IOException::class)
fun Path.search(query: String, intervalMillis: Long, listener: (List<Path>) -> Unit) {
    val searchResults = mutableListOf<Path>()
    if (isDirectory()){
        val files = toFile().listFiles()
        if (files != null){
            for (file in files){
                if (isDirectory()){
                    searchResults.addAll(searchDirectory(this, query))
                } else{
                    if (file.name.contains(query, ignoreCase = true)){
                        searchResults.add(file.toPath())
                    }
                }
            }
        }
    }
    listener(searchResults)
}

private fun searchDirectory(directory: Path, query: String): List<Path> {
    val results = mutableListOf<Path>()
    if (directory.isDirectory()) {
        val files = directory.toFile().listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    results.addAll(searchDirectory(file.toPath(), query))
                } else {
                    if (file.name.contains(query, ignoreCase = true)) {
                        results.add(file.toPath())
                    }
                }
            }
        }
    }
    return results
}