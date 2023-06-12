package com.etb.filemanager.files.file.common.mime


class MimeTypeUtil {


    fun getIconByMimeType(mimeType: String, path: String): Int {
        val mimeTypeObj = MimeType(mimeType)

        val icon: MimeTypeIcon = mimeTypeObj.icon
        val iconResourceId: Int = icon.resourceId
        return iconResourceId


    }


}