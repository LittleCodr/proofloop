package com.proofloop.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.proofloop.MainActivity
import com.proofloop.R

object ProofLoopNotificationManager {

    const val CHANNEL_ID_MISSIONS = "proofloop_missions"
    private const val CHANNEL_NAME_MISSIONS = "Mission & Loop Updates"
    private const val CHANNEL_DESC_MISSIONS = "Real-time updates on connection requests, agreed next actions, and verified loop closures."

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_MISSIONS,
                CHANNEL_NAME_MISSIONS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC_MISSIONS
                enableLights(true)
                lightColor = Color.parseColor("#E5B8FF") // Electric Lilac
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun showMissionNotification(
        context: Context,
        title: String,
        body: String,
        connectionId: String? = null,
        requestId: String? = null,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (connectionId != null) putExtra("connectionId", connectionId)
            if (requestId != null) putExtra("requestId", requestId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_MISSIONS)
            .setSmallIcon(R.drawable.proofloop_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(0xFF15121F.toInt()) // Midnight Ink
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Notification permission not granted yet by user on Android 13+
        }
    }
}
