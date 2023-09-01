/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - StorageItem.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.storage.model

import java.nio.file.Path
import kotlin.io.path.name

data class StorageItem(val path: Path, val name: String = path.name)
