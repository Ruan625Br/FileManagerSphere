package com.etb.filemanager.files.file.properties

import android.os.Parcel
import android.os.Parcelable


data class FileProperties(var title: String, var property: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(property)
    }

    companion object CREATOR : Parcelable.Creator<FileProperties> {
        override fun createFromParcel(parcel: Parcel): FileProperties {
            return FileProperties(parcel)
        }

        override fun newArray(size: Int): Array<FileProperties?> {
            return arrayOfNulls(size)
        }
    }
}
