/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - Stateful.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.util

sealed class Stateful<T> {
    abstract val value: T?
}

data class Loading<T>(override val value: T?): Stateful<T>()

data class Failure<T>(override val value: T?, val throwable: Throwable): Stateful<T>()

data class Success<T>(override val value: T?): Stateful<T>()