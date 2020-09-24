package com.message.messagingmanager.view.activity.messages.specifiedconversation

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import androidx.recyclerview.widget.LinearLayoutManager
import com.message.messagingmanager.R
import kotlinx.android.synthetic.main.activity_specified_message.*
import kotlinx.android.synthetic.main.app_bar.*

class SpecifiedMessageActivity : AppCompatActivity() {

    val array = arrayOf(Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.DATE, Telephony.Sms.BODY)
    val list = ArrayList<Sms>()

    private var smsReceiverName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specified_message)

        smsReceiverName = intent.getStringExtra("SmsReceiverName")!!

        toolbar.title = smsReceiverName
        setSupportActionBar(toolbar)

        getMessage()
    }

    fun getMessage(){
        val uri = Uri.parse("content://sms")
//        val uri = Uri.parse("content://sms/inbox")
//        val uri = Uri.parse("content://sms/sent")

        val cursor = contentResolver.query(uri, array, null, null, Telephony.Sms.DATE)

        cursor!!.moveToFirst()

        val id = cursor.getColumnIndex(Telephony.Sms._ID)
        val body = cursor.getColumnIndex(Telephony.Sms.BODY)
        val address = cursor.getColumnIndex(Telephony.Sms.ADDRESS)
        val date = cursor.getColumnIndex(Telephony.Sms.DATE)

        do {
            if (cursor.getString(address) == smsReceiverName){
                list.add(Sms(cursor.getLong(id), cursor.getString(body), cursor.getString(address), cursor.getString(date)))
            }
        } while (cursor.moveToNext())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ListAdapter(list)
    }
}