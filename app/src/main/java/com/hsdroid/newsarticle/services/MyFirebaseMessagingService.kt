package com.hsdroid.newsarticle.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hsdroid.newsarticle.R

const val CHANNEL_ID = "HSDROID"

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        createNotificationChannel()

        //Fetch all details
        message.notification?.let {
            val bmp = Glide.with(this).asBitmap().load(it.imageUrl).submit().get()
            showNotification(it.title!!, it.body!!, bmp)
        }
    }

    //Show notification to user
    private fun showNotification(title: String, body: String, bitmap: Bitmap) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText(body)
            .setContentTitle(title)
            .setLargeIcon(bitmap)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        try {
            NotificationManagerCompat.from(this).notify(147, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Test channel", NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}