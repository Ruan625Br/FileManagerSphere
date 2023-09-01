package com.etb.filemanager.files.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.pathString


fun Context.getPackageApk(path: Path): String {
    val apkFilePath = path.pathString
    val packageInfo =
        packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_META_DATA)
    val packageName = packageInfo?.packageName.toString()

    return packageName
}


suspend fun getInstalledApkInfo(context: Context, appFilter: AppFilter): List<AppInfo> =
    withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val installedPackages = pm.getInstalledPackages(PackageManager.GET_META_DATA)

        val apkInfoList = mutableListOf<AppInfo>()

        for (packageInfo in installedPackages) {
            val isSystemApp = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
            if (appFilter == AppFilter.NON_SYSTEM && isSystemApp) {
                continue
            }

            val appName = packageInfo.applicationInfo.loadLabel(pm).toString()
            val appIcon = packageInfo.applicationInfo.loadIcon(pm)
            val packageName = packageInfo.packageName
            val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)

            apkInfoList.add(
                AppInfo(
                    appName = appName,
                    appIcon = appIcon,
                    packageName = packageName,
                    isSystemApp = isSystemApp
                )
            )
        }

        return@withContext apkInfoList
    }


suspend fun getUninstalledApkInfo(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
    val apkPaths = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getUninstalledApkPath(context)
    } else {
        getUninstalledApkPathsFromExternalStorage(context)
    }
    val pm = context.packageManager

    val apkInfoList = mutableListOf<AppInfo>()

    for (path in apkPaths) {
        val packageInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_META_DATA)
        if (packageInfo != null) {
            val appName = packageInfo.applicationInfo.loadLabel(pm).toString()
            val appIcon = packageInfo.applicationInfo.loadIcon(pm)
            val packageName = packageInfo.packageName
            val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)

            apkInfoList.add(
                AppInfo(
                    appName = appName,
                    appIcon = appIcon,
                    packageName = packageName,
                    apkPath = path
                )
            )
        }
    }

    return@withContext apkInfoList
}

@RequiresApi(Build.VERSION_CODES.Q)
fun getUninstalledApkPath(context: Context): List<String> {
    val uninstalledApkPaths = mutableListOf<String>()

    val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
    val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} = ?"
    val selectionArgs = arrayOf("application/vnd.android.package-archive")
    val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

    val queryUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    context.contentResolver.query(queryUri, projection, selection, selectionArgs, sortOrder)
        ?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            while (cursor.moveToNext()) {
                val filePath = cursor.getString(dataColumn)
                uninstalledApkPaths.add(filePath)
            }
        }

    return uninstalledApkPaths
}

fun getUninstalledApkPathsFromExternalStorage(context: Context): List<String> {
    val uninstalledApkPaths = mutableListOf<String>()

    val externalStorageDir = context.getExternalFilesDir(null)
    if (externalStorageDir != null && externalStorageDir.exists()) {
        val apkFiles = externalStorageDir.listFiles { file ->
            file.isFile && file.name.lowercase().endsWith(".apk")
        }
        apkFiles?.forEach { file ->
            uninstalledApkPaths.add(file.absolutePath)
        }
    }

    return uninstalledApkPaths
}

fun openApp(context: Context, packageName: String) {
    val packageManager = context.packageManager
    val intent = packageManager.getLaunchIntentForPackage(packageName)

    if (intent != null) {
        context.startActivity(intent)
    } else {

    }
}

fun uninstallApp(context: Context, packageName: String) {
    val uri = Uri.fromParts("package", packageName, null)
    val uninstallIntent = Intent(Intent.ACTION_DELETE, uri)
    context.startActivity(uninstallIntent)
}

fun openAppSettings(context: Context, packageName: String) {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    }
    context.startActivity(intent)
}

fun installApk(context: Context, apkFilePath: Path) {

    // val uri = apkFilePath.fileProviderUri
    val uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".fileprovider",
        apkFilePath.toFile()
    )

    val installIntent = Intent(Intent.ACTION_VIEW)
    installIntent.setDataAndType(uri, "application/vnd.android.package-archive")
    installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(installIntent)

}

enum class AppFilter {
    ALL,
    NON_SYSTEM,
    UNINSTALLED_INTERNAL
}

private fun isSystemPackage(applicationInfo: ApplicationInfo): Boolean {
    return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
}

fun getAppMetadata(appInfo: AppInfo, context: Context, appIsInstalled: Boolean): MutableList<AppMetadata> {
    val packageManager = context.packageManager
    val appMetadataList = mutableListOf<AppMetadata>()
    val packageInfo = getPackageInfo(appIsInstalled, appInfo, packageManager)

    if (packageInfo != null) {
        val info = packageInfo.applicationInfo

        val longVersionCode = PackageInfoCompat.getLongVersionCode(packageInfo)
        val isSystemApp: Boolean = (info.flags and ApplicationInfo.FLAG_SYSTEM) != 0

        val versionName: String = packageInfo.versionName ?: ""
        val versionCode = longVersionCode.toInt().toString()
        val targetSdkVersion = info.targetSdkVersion.toString()
        val minSdkVersion = info.minSdkVersion.toString()
        val compileSdkVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            info.compileSdkVersion
        } else {
           ""
        }.toString()
        val compileSdkVersionCodename = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            info.compileSdkVersionCodename
        } else {
           ""
        }.toString()

        appMetadataList.add(
            AppMetadata(
                label = "Is System App",
                content = if (isSystemApp) "Yes" else "No"
            )
        )
        appMetadataList.add(AppMetadata(label = "Version name", content = versionName))
        appMetadataList.add(AppMetadata(label = "Version code", content = versionCode))
        appMetadataList.add(
            AppMetadata(
                label = "Min Sdk Version",
                content = minSdkVersion
            )
        )
        appMetadataList.add(
            AppMetadata(
                label = "Target Sdk Version",
                content = targetSdkVersion
            )
        )
        appMetadataList.add(
            AppMetadata(
                label = "Compile Sdk Version",
                content = compileSdkVersion
            )
        )
        appMetadataList.add(
            AppMetadata(
                label = "Compile Sdk Version Code name",
                content = compileSdkVersionCodename
            )
        )

    }

    return appMetadataList
}




fun getPackageInfo(
    appIsInstalled: Boolean,
    appInfo: AppInfo,
    packageManager: PackageManager
): PackageInfo? {
    val packageInfo = if (appIsInstalled) {
        packageManager.getPackageInfo(appInfo.packageName, 0)

    } else {
        packageManager.getPackageArchiveInfo(appInfo.apkPath!!, PackageManager.GET_META_DATA)

    }

    return packageInfo
}

data class AppInfo(
    val appName: String,
    val appIcon: Drawable,
    val packageName: String,
    val apkPath: String? = null,
    val isSystemApp: Boolean = false
)

data class AppMetadata(
    val label: String,
    val content: String,
)
