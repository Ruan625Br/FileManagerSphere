package com.etb.filemanager.files.provider.archive.common.mime


import android.annotation.SuppressLint
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.DocumentsContract

@SuppressLint("ParcelCreator")
@JvmInline
value class MimeType(val value: String) : Parcelable {
    val type: String
        get() = value.substring(0, value.indexOf('/'))

    val subtype: String
        get() {
            val indexOfSlash = value.indexOf('/')
            val indexOfSemicolon = value.indexOf(';')
            return value.substring(
                indexOfSlash + 1, if (indexOfSemicolon != -1) indexOfSemicolon else value.length
            )
        }

    val suffix: String?
        get() {
            val indexOfPlus = value.indexOf('+')
            if (indexOfPlus == -1) {
                return null
            }
            val indexOfSemicolon = value.indexOf(';')
            if (indexOfSemicolon != -1 && indexOfPlus > indexOfSemicolon) {
                return null
            }
            return value.substring(
                indexOfPlus + 1, if (indexOfSemicolon != -1) indexOfSemicolon else value.length
            )
        }

    val parameters: String?
        get() {
            val indexOfSemicolon = value.indexOf(';')
            return if (indexOfSemicolon != -1) value.substring(indexOfSemicolon + 1) else null
        }

    fun match(mimeType: MimeType): Boolean =
        type.let { it == "*" || mimeType.type == it }
                && subtype.let { it == "*" || mimeType.subtype == it }
                && parameters.let { it == null || mimeType.parameters == it }

    companion object {
        val ANY = "*/*".asMimeType()
        val APK = "application/vnd.android.package-archive".asMimeType()
        val DIRECTORY = DocumentsContract.Document.MIME_TYPE_DIR.asMimeType()
        val IMAGE_ANY = "image/*".asMimeType()
        val IMAGE_JPEG = "image/jpeg".asMimeType()
        val IMAGE_PNG = "image/png".asMimeType()
        val IMAGE_WEBP = "image/webp".asMimeType()
        val IMAGE_GIF = "image/gif".asMimeType()
        val IMAGE_SVG_XML = "image/svg+xml".asMimeType()
        val VIDEO_MP4 = "video/mp4".asMimeType()
        val PDF = "application/pdf".asMimeType()
        val TEXT_PLAIN = "text/plain".asMimeType()
        val GENERIC = "application/octet-stream".asMimeType()

        fun of(type: String, subtype: String, parameters: String?): MimeType =
            "$type/$subtype${if (parameters != null) ";$parameters" else ""}".asMimeType()

        @JvmField
        val CREATOR: Parcelable.Creator<MimeType> = object : Parcelable.Creator<MimeType> {
            override fun createFromParcel(source: Parcel): MimeType {
                return MimeType(source.readString() ?: throw IllegalArgumentException("Invalid MimeType value"))
            }

            override fun newArray(size: Int): Array<MimeType?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun describeContents(): Int {
    return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(value)
    }
}

fun String.asMimeTypeOrNull(): MimeType? = if (isValidMimeType) MimeType(this) else null

fun String.asMimeType(): MimeType {
    require(isValidMimeType)
    return MimeType(this)
}

private val String.isValidMimeType: Boolean
    get() {
        val indexOfSlash = indexOf('/')
        if (indexOfSlash == -1 || indexOfSlash !in 1 until length) {
            return false
        }
        val indexOfSemicolon = indexOf(';')
        if (indexOfSemicolon != -1) {
            if (indexOfSemicolon !in indexOfSlash + 2 until length) {
                return false
            }
        }
        val indexOfPlus = indexOf('+')
        if (indexOfPlus != -1 && !(indexOfSemicolon != -1 && indexOfPlus > indexOfSemicolon)) {
            if (indexOfPlus !in indexOfSlash + 2
                until if (indexOfSemicolon != -1) indexOfSemicolon - 1 else length) {
                return false
            }
        }
        return true
    }
fun MimeType.isASpecificTypeOfMime(mimeType: MimeType): Boolean = this.value == mimeType.value

fun MimeType.isMedia(): Boolean {
    val mediaMimeTypes = setOf(
        MimeType.IMAGE_ANY,
        MimeType.IMAGE_JPEG,
        MimeType.IMAGE_PNG,
        MimeType.IMAGE_WEBP,
        MimeType.IMAGE_GIF,
        MimeType.VIDEO_MP4
    )
    return this in mediaMimeTypes
}

