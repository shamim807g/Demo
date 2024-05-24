package com.lengo.data.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.lengo.common.di.ApplicationScope
import com.lengo.data.R
import com.lengo.data.repository.ImageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import logcat.logcat

@HiltWorker
class LectionImageWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @ApplicationScope val appScope: CoroutineScope,
    private val imageRepository: ImageRepository
) : CoroutineWorker(appContext, workerParams) {

    private val TAG = "LENGO-WORKER"

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                SyncDataWorker.NOTIFICATION_ID, createNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(SyncDataWorker.NOTIFICATION_ID, createNotification())
        }
    }

    private fun createNotification(): Notification {
        val notification = NotificationCompat.Builder(
            applicationContext, CHANNEL_ID
        )
            .setContentTitle("Syncing Data...")
            .setTicker("Syncing Data...")
            .setSmallIcon(R.drawable.ic_splash)
            .setSilent(true)
            .setOngoing(true)
        // 3
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(notification, CHANNEL_ID)
        }
        return notification.build()
    }

    override suspend fun doWork(): Result {
        logcat(TAG) { "LectionImageWorker WORK START!!!!!" }
        val lecArray = inputData.getStringArray("userPackLec")
        val lectionList = lecArray?.toMutableList() ?: emptyList()


        lectionList.forEach { lec ->
            logcat(TAG) { "$lec" }
            val lecdata = lec.split("-")
            logcat(TAG) { "owner = ${lecdata[0].toLong()} type = ${lecdata[1]} pck = ${lecdata[2].toLong()} " +
                    "lec = ${lecdata[3].toLong()} lng = ${lecdata[4]} text = ${lecdata[5]}" }
            imageRepository.updateUserLectionImage(
                lecdata[0].toLong(),
                lecdata[1],
                lecdata[2].toLong(),
                lecdata[3].toLong(),
                lecdata[4],
                lecdata[5]
            )
        }

        logcat(TAG) { "LectionImageWorker WORK END!!!!!" }
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(
        notificationBuilder: NotificationCompat.Builder,
        id: String
    ) {
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
        val channel = NotificationChannel(
            id, "WorkManagerApp", NotificationManager.IMPORTANCE_LOW
        )
        channel.setSound(null, null)
        channel.description = "WorkManagerApp Notifications"
        channel.setShowBadge(false)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "workSync"
        const val NOTIFICATION_ID = 111
    }
}