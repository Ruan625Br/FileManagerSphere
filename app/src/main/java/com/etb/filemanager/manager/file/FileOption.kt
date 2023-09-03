/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FileOption.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.file

 class FileAction(var icon: Int, var title: String, var action: CreateFileAction){
 }

enum class CreateFileAction{
    OPEN_WITH,
    SELECT,
    EXTRACT,
    CUT,
    COPY,
    DELETE,
    RENAME,
    COMPRESS,
    SHARE,
    COPY_PATH,
    PROPERTIES
}



