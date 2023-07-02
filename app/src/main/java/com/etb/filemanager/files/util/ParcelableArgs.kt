package com.etb.filemanager.files.util

import android.os.Bundle
import android.os.Parcelable
import kotlin.reflect.KClass

interface ParcelableArgs : Parcelable
fun <Args : ParcelableArgs> Bundle.getArgs(argsClass: KClass<Args>): Args =
    getArgsOrNull(argsClass)!!

inline fun <reified Args : ParcelableArgs> Bundle.getArgs() = getArgs(Args::class)

fun <Args : ParcelableArgs> Bundle.getArgsOrNull(argsClass: KClass<Args>): Args? =
    getParcelableSafe(argsClass.java.name)