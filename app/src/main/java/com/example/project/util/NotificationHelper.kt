package com.example.project.util

import android.Manifest
import android.app.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

private const val CHANNEL_ID = "loan_reminder_channel"
private const val CHANNEL_NAME = "Υπενθύμιση Δανεισμών"

fun sendNotification(context: Context, bookTitle: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return // Δεν έχει άδεια -> δεν στέλνουμε notification
        }
    }

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("Προσοχή!")
        .setContentText("Έχεις καθυστερήσει το βιβλίο: $bookTitle")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    notificationManager.notify(bookTitle.hashCode(), notification)
}

fun checkAndRequestNotificationPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
    }
}
