package com.message.messagingmanager.view.receiver

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.message.messagingmanager.HomeActivity
import com.message.messagingmanager.R

class Notification(
    context: Context,
    private var smsId: String,
    private var smsReceiverName: String,
    private var smsReceiverNumber: String,
    private var smsMsg: String,
    private var smsDate: String,
    private var smsTime: String,
    private var smsStatus: String,
    private var smsType: String,
    private var userID: String,
    private var msg: String) : ContextWrapper(context) {

    private val channelID = "messaging"
    private val description = "Test Notification"
    private var uniqueInt = (System.currentTimeMillis() and 0xfffffff).toInt()

    private var mManager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel =
            NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)
        channel.lightColor = Color.GREEN
        channel.enableVibration(true)
        getManager().createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager {

        if (mManager == null) {
            mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return mManager as NotificationManager
    }

    fun getChannelNotification(): NotificationCompat.Builder {

        val intent = Intent(applicationContext, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent =
            PendingIntent.getActivity(this, uniqueInt, intent, PendingIntent.FLAG_ONE_SHOT)

        return NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(getText(R.string.app_name))
            .setContentText(smsMsg + "\n" + msg) // getText(R.string.openDialogue)
            .setSmallIcon(R.mipmap.ic_launcher_sms)
            .setContentIntent(pendingIntent)
    }
}