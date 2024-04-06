/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ScreenshotObserverService.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.files.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore.Images.Media
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.etb.filemanager.R
import com.etb.filemanager.compose.core.navigation.ChatRoute
import com.etb.filemanager.compose.feature.presentation.HomeScreen
import com.etb.filemanager.files.extensions.toBitmap
import com.etb.filemanager.files.util.FileUtil
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.pathString

class ScreenshotObserverService : Service() {

    private lateinit var screenshotObserver: ScreenshotObserver
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        screenshotObserver = ScreenshotObserver(applicationContext)
        registerScreenshotObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterScreenshotObserver()
    }

    private fun registerScreenshotObserver(){
        val contentObserver = applicationContext.contentResolver
        contentObserver.registerContentObserver(
           Media.EXTERNAL_CONTENT_URI,
            true,
            screenshotObserver
        )
    }

    private fun unregisterScreenshotObserver(){
        val contentResolver = applicationContext.contentResolver
        contentResolver.unregisterContentObserver(screenshotObserver)
    }
    inner class ScreenshotObserver(private val context: Context): ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)

            uri?.let {
                val path = Paths.get(FileUtil().getFilePathFromUri(context, it))
                Log.i("ScreenshotObserverService", path.pathString.toString())

                showNotification("Nova imagem detectada: ${path.fileName}", it, path, context)
            }
            Log.i("ScreenshotObserverService", uri.toString())

        }

        private fun showNotification(content: String, uri: Uri?, path: Path, context: Context){
            if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED){
                Log.i("ScreenshotObserverService", "Notfi")

                val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
                notificationManager.createNotificationChannel(notificationChannel)

                val notificationIntent = Intent(context, HomeScreen::class.java).apply {
                    putExtra("startDestination", ChatRoute)
                    putExtra(SCREENSHOT_PATH, path.pathString)
                }
                val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)

                val action: NotificationCompat.Action =
                    NotificationCompat.Action.Builder(
                        R.drawable.file_image_icon,
                        "Rename image",
                        pendingIntent
                    ).build()

                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(content)
                    .setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(uri?.toBitmap(context)))
                    .setAutoCancel(false)
                    .addAction(action)


                notificationManager.notify(NOTIFICATION_ID, builder.build())
                startForeground(NOTIFICATION_ID, builder.build())
            }
        }

    }

    companion object {
        const val CHANNEL_ID = "screenshot_channel_id"
        const val CHANNEL_NAME = "screenshot_channel_name"
        const val NOTIFICATION_ID = 626
        const val SCREENSHOT_PATH = "screenshot_PATH"
    }
}