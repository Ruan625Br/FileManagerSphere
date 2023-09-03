/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - DebouncedRunnable.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.util

/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */


import android.os.Handler

class DebouncedRunnable(
    private val handler: Handler,
    private val intervalMillis: Long,
    block: () -> Unit
) : () -> Unit {
    private val lock = Any()

    private val runnable = Runnable(block)

    override operator fun invoke() {
        synchronized(lock) {
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, intervalMillis)
        }
    }

    fun cancel() {
        synchronized(lock) { handler.removeCallbacks(runnable) }
    }
}