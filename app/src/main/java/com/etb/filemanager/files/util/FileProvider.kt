/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FileProvider.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.util

import android.content.ContentResolver
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.IOException
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths



val Path.isDocumentPath: Boolean
    get(){
        return Files.isRegularFile(this)
    }

val Path.fileProviderUri: Uri
    get() {
        if (isDocumentPath) {
            try {
                val documentUri = DocumentFile.fromFile(toFile())
                return documentUri.uri
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val uriPath = Uri.encode(toUri().toString())
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority("com.etb.filemanager.file_provider")
            .path(uriPath)
            .build()
    }

 val Uri.fileProviderPath: Path
    get() {
        val uriPath = Uri.decode(path).substring(1)
        return Paths.get(URI.create(uriPath))
    }