package com.message.messagingmanager.view.activity

import android.annotation.SuppressLint
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Contacts
import com.message.messagingmanager.view.adapter.ContactsEditAdapter
import kotlinx.android.synthetic.main.activity_contacts_edit.*
import kotlinx.android.synthetic.main.app_bar.*

class ContactsEditActivity : AppCompatActivity() {

    private var c: Cursor? = null
    private var arrContacts: ArrayList<Contacts> = ArrayList()

    private var smsId: String = ""
    private var smsReceiverName: String = ""
    private var smsReceiverNumber: String = ""
    private var smsMsg: String = ""
    private var smsDate: String = ""
    private var smsTime: String = ""
    private var smsStatus: String = ""
    private var smsType: String = ""
    private var userID: String = ""
    private var calendar: Long = 0

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_edit)

        toolbar.title = "Select Receiver"
        setSupportActionBar(toolbar)

        smsId = intent.getStringExtra("SmsId")
        smsReceiverName = intent.getStringExtra("SmsReceiverName")
        smsReceiverNumber = intent.getStringExtra("SmsReceiverNumber")
        smsMsg = intent.getStringExtra("SmsMsg")
        smsDate = intent.getStringExtra("SmsDate")
        smsTime = intent.getStringExtra("SmsTime")
        smsStatus = intent.getStringExtra("SmsStatus")
        smsType = intent.getStringExtra("SmsType")
        userID = intent.getStringExtra("UserID")
        calendar = intent.extras!!.getLong("calendar")

        getContacts()
    }

    @SuppressLint("WrongConstant", "Recycle")
    private fun getContacts() {
        progressBar.visibility = View.VISIBLE
        c = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        while (c!!.moveToNext()) {
            val name =
                c!!.getString(c!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber =
                c!!.getString(c!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            val contact = Contacts(name, phoneNumber)

            if (arrContacts.size > 0) {
                if (arrContacts[arrContacts.size - 1].getName() != name || !arrContacts[arrContacts.size - 1].getPhone().startsWith(phoneNumber.subSequence(0, 3))){
                    arrContacts.add(contact)
                }
            } else {
                arrContacts.add(contact)
            }
        }
        c!!.close()

        val adapter = ContactsEditAdapter(arrContacts, this, smsId, smsMsg, smsDate,
            smsTime, smsStatus, smsType, userID, calendar)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.adapter = adapter

        progressBar.visibility = View.GONE
    }
}
