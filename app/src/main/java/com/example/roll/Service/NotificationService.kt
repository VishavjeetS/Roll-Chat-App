package com.example.roll.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.example.roll.R
import com.example.roll.Util.MessageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class NotificationService: FirebaseMessagingService() {

    private val CHANNEL_ID = "1000"
    private val NOTIFICATION_ID = 100

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if(remoteMessage.data.size>0){
            var map : Map<String, String> = remoteMessage.data
            Log.d("TAG", "onMessageReceived: Chat Notification")

            val title = map["title"]
            val message = map["message"]
            val uID = map["hisID"]
            val chatID = map["chatID"]

            Log.d("TAG", "onMessageReceived: chatID is $chatID\n hisID$uID")

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                createOreoNotification(title, message, uID, chatID)
            else
                createNormalNotification(title, message, uID, chatID)
        }
        super.onMessageReceived(remoteMessage)
    }

    private fun createNormalNotification(title: String?, message: String?, uID: String?, chatID: String?) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder= NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(uri)

        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("chatID", chatID)
        intent.putExtra("uID", uID)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        notificationBuilder.setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65), notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createOreoNotification(title: String?, message: String?, uID: String?, chatID: String?) {
        val channel = NotificationChannel(CHANNEL_ID, "Message", NotificationManager.IMPORTANCE_HIGH)
        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("uID", uID)
        intent.putExtra("chatID", chatID)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        manager.notify(100, notification)
    }

    override fun onNewToken(token: String) {
        updateToken(token)
        super.onNewToken(token)
    }

    private fun updateToken(token: String) {
        val mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser!=null){
            val dbref =FirebaseDatabase.getInstance().getReference("user").child(mAuth.currentUser!!.uid)
            val map = mutableMapOf<String, Any>()
            map["token"] = token
            dbref.updateChildren(map)
        }
    }
}