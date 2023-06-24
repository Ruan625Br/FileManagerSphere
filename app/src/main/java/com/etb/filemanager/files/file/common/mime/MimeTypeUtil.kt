package com.etb.filemanager.files.file.common.mime

import java.nio.file.Path


class MimeTypeUtil {


    fun getIconByMimeType(mimeType: String): Int {
        val mimeTypeObj = MimeType(mimeType)

        val icon: MimeTypeIcon = mimeTypeObj.icon
        val iconResourceId: Int = icon.resourceId
        return iconResourceId


    }


    fun isSpecificFileType(mimeType: String, type: MimeTypeIcon): Boolean {
        val iconMimeType = getIconByMimeType(mimeType)

        return iconMimeType == type.resourceId
    }


}