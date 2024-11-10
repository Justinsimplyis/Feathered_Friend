package com.ice_opscpoe.featheredfriends

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import android.net.Uri
import android.provider.Settings

object NotificationUtils {
    private const val CHANNEL_ID = "goal_completed_channel"
    private const val CHANNEL_NAME = "Goal Completion Notifications"
    private const val NOTIFICATION_ID = 1


    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Set the notification channel's importance and sound
            val soundUri: Uri = Settings.System.DEFAULT_NOTIFICATION_URI
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification for goal completion"
                enableLights(true)
                enableVibration(true)
                lightColor = android.graphics.Color.RED
                setSound(soundUri, null) // Set the default notification sound
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showGoalCompletedNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //this created ths notification
        val notification: Notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Goal Completed!")
            .setContentText("Congratulations! You have completed your bird observation goal.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Dismiss notification when tapped
            .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Congratulations! You have completed your bird observation goal."))
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
