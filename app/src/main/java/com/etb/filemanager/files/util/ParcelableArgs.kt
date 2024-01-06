/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ParcelableArgs.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.util

/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */


import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import kotlin.reflect.KClass


interface ParcelableArgs : Parcelable

fun <Args : ParcelableArgs> Intent.putArgs(args: Args, argsClass: KClass<Args>): Intent =
    putExtra(argsClass.java.name, args)

inline fun <reified Args : ParcelableArgs> Intent.putArgs(args: Args) = putArgs(args, Args::class)


fun <Args : ParcelableArgs> Bundle.getArgsOrNull(argsClass: KClass<Args>): Args? =
    getParcelable(argsClass.java.name)

inline fun <reified Args : ParcelableArgs> Bundle.getArgsOrNull() = getArgsOrNull(Args::class)


@Parcelize
class Args(val savedInstanceState: @WriteWith<BundleParceler> Bundle?) : ParcelableArgs