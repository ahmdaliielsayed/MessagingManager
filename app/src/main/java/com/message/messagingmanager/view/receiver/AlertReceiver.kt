package com.message.messagingmanager.view.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import android.widget.Toast
import com.message.messagingmanager.R
import android.app.Activity
import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.message.messagingmanager.WhatsappAccessibilityService
import com.message.messagingmanager.model.Message
import com.message.messagingmanager.ui.upcoming.UpcomingViewModel
import java.lang.Exception
import java.net.URLEncoder

class AlertReceiver: BroadcastReceiver() {

    private lateinit var sentPendingIntent: PendingIntent
    private lateinit var deliveredPendingIntent: PendingIntent
    private lateinit var smsSentReceiver: BroadcastReceiver
    private lateinit var smsDeliveredReceiver: BroadcastReceiver

    private lateinit var smsId: String
    private lateinit var smsReceiverName: String
    private lateinit var smsReceiverNumber: String
    private lateinit var smsMsg: String
    private lateinit var smsDate: String
    private lateinit var smsTime: String
    private lateinit var smsStatus: String
    private lateinit var smsType: String
    private lateinit var userID: String
    private var calendar: Long = 0

    private lateinit var databaseReferenceMsg: DatabaseReference

    override fun onReceive(context: Context?, intent: Intent?) {

        context!!.startService(Intent(context.applicationContext, WhatsappAccessibilityService::class.java))

        sentPendingIntent = PendingIntent.getBroadcast(context, 0, Intent(context!!.resources.getString(R.string.msg_sent)), 0)
        deliveredPendingIntent = PendingIntent.getBroadcast(context, 0, Intent(context.resources.getString(R.string.msg_delivered)), 0)

        smsId = intent?.extras?.getString("SmsId")!!
        smsReceiverName = intent.extras?.getString("SmsReceiverName")!!
        smsReceiverNumber = intent.extras?.getString("SmsReceiverNumber")!!
        smsMsg = intent.extras?.getString("SmsMsg")!!
        smsDate = intent.extras?.getString("SmsDate")!!
        smsTime = intent.extras?.getString("SmsTime")!!
        smsStatus = intent.extras?.getString("SmsStatus")!!
        smsType = intent.extras?.getString("SmsType")!!
        userID = intent.extras?.getString("UserID")!!
        calendar = intent.extras!!.getLong("calendar")

        databaseReferenceMsg = FirebaseDatabase.getInstance().reference.child("Users").child(userID).child("Messages")

        if (smsType == "SMS") {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(smsReceiverNumber, null, smsMsg, sentPendingIntent, deliveredPendingIntent)

            /////////////////////////////////////////////////////////////////////////////////////////////////////

            smsSentReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, arg1: Intent) {
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            Toast.makeText(context, R.string.msg_sent, Toast.LENGTH_SHORT).show()

                            // update msg on fireBase
                            updateMsg(context!!.resources.getString(R.string.status_sent), context.resources.getString(R.string.type_sms))
                            // show notification
                            showNotification(context, context.resources.getString(R.string.msg_sent))
                        }
                        SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                            Toast.makeText(context, R.string.msg_generic_failure, Toast.LENGTH_SHORT).show()

                            // update msg on fireBase
                            updateMsg(context!!.resources.getString(R.string.msg_generic_failure), context.resources.getString(R.string.type_sms))
                            // show notification
                            showNotification(context, context.resources.getString(R.string.msg_generic_failure))
                        }
                        SmsManager.RESULT_ERROR_NO_SERVICE -> {
                            Toast.makeText(context, R.string.msg_no_service, Toast.LENGTH_SHORT).show()

                            // update msg on fireBase
                            updateMsg(context!!.resources.getString(R.string.msg_no_service), context.resources.getString(R.string.type_sms))
                            // show notification
                            showNotification(context, context.resources.getString(R.string.msg_no_service))
                        }
                        SmsManager.RESULT_ERROR_NULL_PDU -> {
                            Toast.makeText(context, R.string.msg_null_pdu, Toast.LENGTH_SHORT).show()

                            // update msg on fireBase
                            updateMsg(context!!.resources.getString(R.string.msg_null_pdu), context.resources.getString(R.string.type_sms))
                            // show notification
                            showNotification(context, context.resources.getString(R.string.msg_null_pdu))
                        }
                        SmsManager.RESULT_ERROR_RADIO_OFF -> {
                            // no signal (مفيش شبكة أو وضع الطيران)
                            Toast.makeText(context, R.string.msg_radio_off, Toast.LENGTH_SHORT).show()

                            // update msg on fireBase
                            updateMsg(context!!.resources.getString(R.string.msg_radio_off), context.resources.getString(R.string.type_sms))
                            // show notification
                            showNotification(context, context.resources.getString(R.string.msg_radio_off))
                        }
                    }
                }
            }

            smsDeliveredReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, arg1: Intent) {
                    when (resultCode){
                        Activity.RESULT_OK -> {
                            Toast.makeText(context, R.string.msg_delivered, Toast.LENGTH_SHORT).show()

                            // update msg on fireBase
                            updateMsg(context!!.resources.getString(R.string.status_delivered), context.resources.getString(R.string.type_sms))
                            // show notification
                            showNotification(context, context.resources.getString(R.string.msg_delivered))
                        }
                        Activity.RESULT_CANCELED -> {
                            Toast.makeText(context, R.string.msg_not_delivered, Toast.LENGTH_SHORT).show()

                            // update msg on fireBase
                            updateMsg(context!!.resources.getString(R.string.msg_not_delivered), context.resources.getString(R.string.type_sms))
                            // show notification
                            showNotification(context, context.resources.getString(R.string.msg_not_delivered))
                        }
                    }
                }
            }

            context.applicationContext.registerReceiver(smsSentReceiver, IntentFilter(context.resources.getString(R.string.msg_sent)))
            context.applicationContext.registerReceiver(smsDeliveredReceiver, IntentFilter(context.resources.getString(R.string.msg_delivered)))

            //////////////////////////////////////////////////////////////////////////////////////////////////////
        } else {
            try {
                // update msg on fireBase
                updateMsg(context.resources.getString(R.string.status_history), context.resources.getString(R.string.type_whats_app))

                WhatsappAccessibilityService.sActive = true
                WhatsappAccessibilityService.sPhone = smsReceiverNumber
                WhatsappAccessibilityService.sContact = smsReceiverName
                WhatsappAccessibilityService.sMsg = smsMsg
                val intent =  Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + smsReceiverNumber.replace("+","") + "&text=" + WhatsappAccessibilityService.sMsg))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)

                // show notification
                showNotification(context, context.resources.getString(R.string.status_history))
            } catch (e: Exception) {
                e.printStackTrace()
                // update msg on fireBase
                updateMsg(context.resources.getString(R.string.msg_no_whats_app_installed), context.resources.getString(R.string.type_whats_app))
                // show notification
                showNotification(context, context.resources.getString(R.string.msg_no_whats_app_installed))
            }
        }
    }

    private fun updateMsg(smsStatus: String, smsType: String){

        val message = Message(smsId, smsReceiverName, smsReceiverNumber, smsMsg, smsDate, smsTime,
            smsStatus, smsType, userID, calendar)
        // update msg on fireBase
        databaseReferenceMsg.child(smsId).setValue(message)
    }

    private fun showNotification(context: Context?, msg: String) {
        val notificationHelper = Notification(context!!, smsId, smsReceiverName, smsReceiverNumber, smsMsg, smsDate, smsTime, smsStatus, smsType, userID, msg)
        val nb = notificationHelper.getChannelNotification()
        notificationHelper.getManager().notify(smsId.hashCode(), nb.build())
    }
}