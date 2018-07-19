package com.example.sport0102.myapplication

import android.app.*
import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.os.Build
import android.content.Context.NOTIFICATION_SERVICE
import android.media.RingtoneManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMessaging"
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // ...

        // Check if message contains a data payload.
        if (remoteMessage!!.data.size > 0) {
            var title = remoteMessage.data.get("title").toString()
            var text = remoteMessage.data.get("text").toString()
            sendNotification(title,text)
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

        }

        // Check if message contains a notification payload.
        if (remoteMessage!!.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private fun sendNotification(title : String,text: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }


}
