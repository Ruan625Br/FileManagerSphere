package com.etb.filemanager.files.provider.archive.common.mime


import android.annotation.SuppressLint
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
        val IMAGE_GIF = "image/gif".asMimeType()
        val IMAGE_SVG_XML = "image/svg+xml".asMimeType()
        val PDF = "application/pdf".asMimeType()
        val TEXT_PLAIN = "text/plain".asMimeType()
        val GENERIC = "application/octet-stream".asMimeType()

        fun of(type: String, subtype: String, parameters: String?): MimeType =
            "$type/$subtype${if (parameters != null) ";$parameters" else ""}".asMimeType()
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
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