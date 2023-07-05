package com.etb.filemanager.manager.files.services


import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.etb.filemanager.R
import com.etb.filemanager.activity.MainActivity
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sourcePath = intent?.getStringArrayListExtra("sourcePaths")?.toList()
        val newNames = intent?.getStringArrayListExtra("newNames")?.toList()
        val destinationPath = intent?.getStringExtra("destinationPath")
        val operation = intent?.getSerializableExtra("operation") as FileOperation

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = createNotificationBuilder()

        startForeground(1, notificationBuilder.build())

        serviceScope.launch {
            performFileOperation(
                applicationContext,
                operation,
                sourcePath,
                newNames,
                true,
                destinationPath.toString(),
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

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationBuilder(): NotificationCompat.Builder {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_LOW
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
                .setProgress(100, 0, true)
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
                .setProgress(100, 0, true)
                .setOngoing(true)

        }
    }

    companion object {
        val CHANEL_ID = "file_operation_channel"
        val CHANEL_NAME = "File Operation"
    }
}
