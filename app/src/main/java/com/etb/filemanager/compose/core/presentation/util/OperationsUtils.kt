/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - OperationsUtils.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.presentation.util


import com.etb.filemanager.compose.core.models.FileOperationResult
import com.etb.filemanager.compose.core.models.OperationResult
import com.etb.filemanager.manager.files.filecoroutine.FileOperation
import com.etb.filemanager.manager.files.filecoroutine.performFileOperation
import com.jn.airesponsematcher.extensions.removeQuotes
import com.jn.airesponsematcher.operation.Operation
import com.jn.airesponsematcher.utils.Patterns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

interface FileOperationCallback {
    fun onResolve(operationResult: FileOperationResult)
}

class FileOperations(scope: CoroutineScope, fileOperationCallback: FileOperationCallback) {

    val operations = listOf(RenameFile(scope, fileOperationCallback), Create(scope, fileOperationCallback), Write(scope, fileOperationCallback))

    data class RenameFile(private val scope: CoroutineScope, private val  callback: FileOperationCallback) : Operation {
        override val name: String
            get() = "renameFile"

        override fun resolve(output: String, args: Map<String, String>?): String {
            val path = args?.get(PATH)
            val newName = args?.get(FILE_NAME)
            if (path == null || newName == null) return output
            val oldName = Path(path).fileName

            scope.launch {
                    performFileOperation(
                        operation = FileOperation.RENAME,
                        sourcePath = listOf(path),
                        newNames = listOf(newName)
                    )
            }
            callback.onResolve(FileOperationResult("Rename File", "Renamed \"$oldName\" to \"$newName\"", OperationResult.SUCCESS))

            return output
        }

    }
    data class Create(private val scope: CoroutineScope, private val callback: FileOperationCallback) : Operation {
        override val name: String
            get() = "create"

        override fun resolve(output: String, args: Map<String, String>?): String {
            val path = args?.get(PATH)
            val name = args?.get(FILE_NAME)
            val isDir = args?.get(IS_DIR).toBoolean()

            if (path == null || name == null) return output
            val pathString = Path(path).resolve(name).absolutePathString()
            val itemType = if (isDir) "Directory" else "File"

            scope.launch {
                    performFileOperation(
                        operation = FileOperation.CREATE,
                        sourcePath = listOf(pathString),
                        createDir = isDir
                    )
            }
            callback.onResolve(FileOperationResult("Create", "Created $itemType \"$name\" in \"$pathString\"", OperationResult.SUCCESS))
            return output
        }

    }

    data class Write(private val scope: CoroutineScope, private val  callback: FileOperationCallback): Operation {


        private val mPattern = "$name\\s*START${Patterns.BASE_ARGUMENT}END"
        override val name: String
            get() = "write"

        override val regex: Regex
            get() = mPattern.toRegex(RegexOption.DOT_MATCHES_ALL)
        override fun resolve(output: String, args: Map<String, String>?): String {
            val path = args?.get(PATH)
            val content = args?.get(CONTENT)?.removeQuotes()

            if (path == null || content == null) return output

            //TODO(move this operation to an appropriate place)
            scope.launch {
                withContext(Dispatchers.IO){
                        File(path).writeText(content)
                }
            }
            callback.onResolve(FileOperationResult("Write", "Wrote content to file at \"$path\"", OperationResult.SUCCESS))

            return output

        }

    }

    companion object {
        const val PATH = "path"
        const val FILE_NAME = "fileName"
        const val IS_DIR = "isDir"
        const val CONTENT = "content"
    }
}