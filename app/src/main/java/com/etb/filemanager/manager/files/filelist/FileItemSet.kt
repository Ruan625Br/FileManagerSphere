/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FileItemSet.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.files.filelist

import java.nio.file.Path
import android.os.Parcel
import android.os.Parcelable

import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.files.compat.writeParcelableListCompat
import com.etb.filemanager.files.util.LinkedMapSet
import com.etb.filemanager.files.util.readParcelableListCompat

class FileItemSet() : LinkedMapSet<String, FileModel>(FileModel::filePath), Parcelable {
    constructor(parcel: Parcel) : this() {
        addAll(parcel.readParcelableListCompat())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelableListCompat(toList(), flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<FileItemSet> {
        override fun createFromParcel(parcel: Parcel): FileItemSet = FileItemSet(parcel)

        override fun newArray(size: Int): Array<FileItemSet?> = arrayOfNulls(size)
    }
}

fun fileItemSetOf(vararg files: FileModel) = FileItemSet().apply { addAll(files) }