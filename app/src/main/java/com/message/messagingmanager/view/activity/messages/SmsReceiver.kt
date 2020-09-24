package com.message.messagingmanager.view.activity.messages

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage

class SmsReceiver : BroadcastReceiver() {

    companion object{
        lateinit var smsListener: SmsListener

        fun setListener(listener: SmsListener) {
            smsListener = listener
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action.equals("android.provider.Telephony.SMS_RECEIVED")) {

            val bundle: Bundle = intent.extras!!
            val msgs: Array<SmsMessage?>
            var msgFrom = ""
            var msgBody = ""
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                msgFrom = smsMessage.displayOriginatingAddress
                msgBody = smsMessage.displayMessageBody
            }
            smsListener.onTextReceived(msgFrom, msgBody)

//            try {
//                val pdus = bundle["pdus"] as Array<*>?
//                msgs = arrayOfNulls(pdus!!.size)
//                for (i in msgs.indices) {
//                    msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
//                    msgFrom = msgs[i]!!.originatingAddress!!
//                    msgBody = msgs[i]!!.messageBody
//
////                    Toast.makeText(context, "From: $msgFrom\nBody: $msgBody", Toast.LENGTH_LONG).show()
//                    smsListener.onTextReceived(msgFrom, msgBody)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
        }
    }

    interface SmsListener {
        fun onTextReceived(address: String?, msg: String?)
    }
}
