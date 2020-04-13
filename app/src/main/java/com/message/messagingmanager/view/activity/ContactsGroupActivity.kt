package com.message.messagingmanager.view.activity

import android.annotation.SuppressLint
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Contacts
import com.message.messagingmanager.view.adapter.ContactsGroupAdapter
import kotlinx.android.synthetic.main.activity_contacts_group.*
import kotlinx.android.synthetic.main.app_bar.*

class ContactsGroupActivity : AppCompatActivity() {

    private var c: Cursor? = null
    private var arrContacts: ArrayList<Contacts> = ArrayList()

    private lateinit var groupId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_group)

        toolbar.setTitle(R.string.createGroup)
        setSupportActionBar(toolbar)

        groupId = intent?.extras?.getString("groupId")!!

        getContacts()
    }

    @SuppressLint("WrongConstant", "Recycle")
    private fun getContacts(){
        c = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC")

        while (c!!.moveToNext()) {
            val name = c!!.getString(c!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = c!!.getString(c!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

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

        val adapter = ContactsGroupAdapter(arrContacts, this, groupId)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)
        recyclerView.adapter = adapter
    }
}
