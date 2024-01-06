/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FragmentExtensions.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.util


import androidx.annotation.ArrayRes
import androidx.annotation.PluralsRes
import androidx.fragment.app.Fragment


fun Fragment.getQuantityString(@PluralsRes id: Int, quantity: Int): String =
    requireContext().getQuantityString(id, quantity)

fun Fragment.getQuantityString(
    @PluralsRes id: Int, quantity: Int, vararg formatArgs: Any?
): String = requireContext().getQuantityString(id, quantity, *formatArgs)


fun Fragment.getStringArray(@ArrayRes id: Int) = requireContext().getStringArray(id)


