package com.message.messagingmanager.view.activity

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SearchView
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

    lateinit var adapter: ContactsEditAdapter
    private var textVal: String = ""

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_edit)

        toolbar.setTitle(R.string.selectReceiver)
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

        adapter = ContactsEditAdapter(arrContacts, this, smsId, smsMsg, smsDate,
            smsTime, smsStatus, smsType, userID, calendar)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.adapter = adapter

        progressBar.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem: MenuItem = menu!!.findItem(R.id.item_search)
        val searchView: SearchView = searchItem.actionView as SearchView

        // to remove search icon from keyboard because we just on locale recyclerview not online recyclerview
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        // to set search text
        searchView.queryHint = getText(R.string.searchName)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                if (newText != null) {
                    textVal = newText
                }
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                adapter.filter.filter(textVal)
                // change color
                val id = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
                val editText = searchView.findViewById<View>(id) as EditText
                editText.setHintTextColor(Color.WHITE)
//                editText.setText("")
//                adapter.filter.filter("")
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                adapter.filter.filter("")
                return true
            }

        })
        return true
    }
}
