/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - FileOperationResult.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.models

import kotlinx.serialization.Serializable

@Serializable
data class FileOperationResult(val name: String, val desc: String, val result: OperationResult)

enum class OperationResult(val value: String){
    SUCCESS("Success"),
    FAILED("Failed")
}
