package com.etb.filemanager.files.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.etb.filemanager.R
import com.etb.filemanager.files.app.packageManager
import com.etb.filemanager.files.provider.archive.common.mime.MimeType
import com.etb.filemanager.files.util.fileProviderUri
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

fun Context.getPackageApk(path: Path): String{
    val apkFilePath = path.pathString
    val packageInfo = packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_META_DATA)
    val packageName = packageInfo?.packageName.toString()

    return packageName
}

@RequiresApi(Build.VERSION_CODES.S)
fun Path.readApkBasicAttributes(context: Context){
    val apkFilePath = pathString
    val packageInfo = packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_META_DATA)

    val nameAPK = packageInfo?.attributions
    val packageAPK = context.getPackageApk(this)
}

