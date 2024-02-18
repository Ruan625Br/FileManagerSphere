/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - MessageUtils.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.presentation.util

import com.jn.airesponsematcher.extensions.replaceAllOperationsWithNewValue
import com.jn.airesponsematcher.operation.OperationBase
import com.jn.airesponsematcher.utils.Patterns

fun String.filterMessage(): String {
    val rename = object  : OperationBase {
        override val operationName: String
            get() = "renameFile"

    }
    val create = object  : OperationBase {
        override val operationName: String
            get() = "create"

    }
    val write = object  : OperationBase {
        private val mPattern = "$operationName\\s*START${Patterns.BASE_ARGUMENT}END"

        override val operationName: String
            get() = "write"
        override val regex: Regex
            get() = mPattern.toRegex(RegexOption.DOT_MATCHES_ALL)
    }
    val operations = listOf(rename, create, write)
    return replaceAllOperationsWithNewValue(operations).trim().removeEmptyLines()
}

private fun String.removeEmptyLines(): String {
    return replace(Regex("\\n\n\\s*\\n"), "\n")
}