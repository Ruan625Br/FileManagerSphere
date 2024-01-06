/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FileOperationService.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.files.services


import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.etb.filemanager.R
import com.etb.filemanager.activity.MainActivity
import com.etb.filemanager.manager.files.filecoroutine.CompressionType
import com.etb.filemanager.manager.files.filecoroutine.FileOperation
import com.etb.filemanager.manager.files.filecoroutine.performFileOperation
import kotlinx.coroutines.*

class FileOperationService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sourcePath = intent?.getStringArrayListExtra("sourcePaths")?.toList()
        val newNames = intent?.getStringArrayListExtra("newNames")?.toList()
        val destinationPath = intent?.getStringExtra("destinationPath")
        val createDir = intent?.getBooleanExtra("createDir", false)
        val compressionType = intent?.getSerializableExtra("compressionType") as? CompressionType
        val operation = intent?.getSerializableExtra("operation") as FileOperation

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = createNotificationBuilder()

        startForeground(1, notificationBuilder.build())

        serviceScope.launch {
            performFileOperation(
                operation,
                sourcePath,
                newNames,
                createDir,
                destinationPath.toString(),
                compressionType,
                { progress ->
                    notificationBuilder.setProgress(100, progress, false)
                    notificationManager.notify(1, notificationBuilder.build())
                },
                { success ->
                    if (success) {
                        notificationBuilder.setContentText("Operação concluída")
                            .setProgress(0, 0, false)
                            .setOngoing(false)
                    } else {
                        notificationBuilder.setContentText("Falha na operação")
                            .setProgress(0, 0, false)
                            .setOngoing(false)
                    }
                    notificationManager.notify(1, notificationBuilder.build())
                    stopForeground(true)
                    stopSelf()
                }
            )
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationBuilder(): NotificationCompat.Builder {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_HIGH
            val chanel = NotificationChannel(CHANEL_ID, CHANEL_NAME, importance)
            notificationManager.createNotificationChannel(chanel)
        }
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val title = "File Operation"
        val mesage = "Operação em andamento..."

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationCompat.Builder(this, CHANEL_ID)
                .setContentTitle(title)
                .setContentText(mesage)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.mipmap.ic_launcher
                    )
                )
                .setContentIntent(pendingIntent)
               .setProgress(100, 0, false)
                .setOngoing(true)

        } else{
            NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(mesage)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.mipmap.ic_launcher
                    )
                )
                .setContentIntent(pendingIntent)
                .setProgress(100, 0, false)
                .setOngoing(true)

        }
    }

    companion object {
        val CHANEL_ID = "file_operation_channel"
        val CHANEL_NAME = "File Operation"
    }
}
