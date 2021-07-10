package com.draco.stingray.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.provider.Settings
import com.draco.stingray.R
import com.draco.stingray.receivers.ScreenStateBroadcastReceiver

class ScreenStateService : Service() {
    companion object {
        const val NOTIFICATION_ID = 1
    }

    private lateinit var notificationManager: NotificationManager
    private val screenStateBroadcastReceiver = ScreenStateBroadcastReceiver()

    /**
     * Look for screen on/off state changes
     */
    private val screenStateIntentFilter = IntentFilter().apply {
        addAction(Intent.ACTION_SCREEN_ON)
        addAction(Intent.ACTION_SCREEN_OFF)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Start listening
     */
    private fun registerReceiver() {
        screenStateBroadcastReceiver.screenStateListener = { state ->
            Settings.Global.putInt(
                contentResolver,
                Settings.Global.AIRPLANE_MODE_ON,
                if (!state) 1 else 0
            )
        }

        registerReceiver(screenStateBroadcastReceiver, screenStateIntentFilter)
    }

    /**
     * Stop listening
     */
    private fun unregisterReceiver() = try {
        unregisterReceiver(screenStateBroadcastReceiver)
    } catch (_: Exception) {}

    /**
     * Create the necessary notification channel
     */
    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(getString(R.string.notif_channel_id)) == null) {
            val notificationChannel = NotificationChannel(
                getString(R.string.notif_channel_id),
                getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    /**
     * Create and show a notification for this foreground service
     */
    private fun createNotification() {
        val notificationSettingsIntent = Intent()
            .setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            notificationSettingsIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(applicationContext, getString(R.string.notif_channel_id))
            .setContentTitle(getString(R.string.notif_channel_content_title))
            .setContentText(getString(R.string.notif_channel_content_text))
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSmallIcon(R.drawable.ic_baseline_incomplete_circle_24)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    /**
     * Stop displaying the notification
     */
    private fun cancelNotification() = notificationManager.cancel(NOTIFICATION_ID)

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        createNotification()
        registerReceiver()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelNotification()
        unregisterReceiver()
    }
}