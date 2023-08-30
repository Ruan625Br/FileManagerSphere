package com.etb.filemanager.manager.files.filelist

import android.os.Parcel
import android.os.Parcelable
import com.etb.filemanager.files.compat.writeParcelableListCompat
import com.etb.filemanager.files.util.LinkedMapSet
import com.etb.filemanager.files.util.readParcelableListCompat
import com.etb.filemanager.manager.adapter.FileModel

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