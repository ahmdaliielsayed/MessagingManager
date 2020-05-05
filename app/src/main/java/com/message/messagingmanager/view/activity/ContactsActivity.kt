package com.message.messagingmanager.view.activity

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Contacts
import com.message.messagingmanager.view.adapter.ContactsAdapter
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.app_bar.*


class ContactsActivity : AppCompatActivity() {

    private var cursorWhatsApp: Cursor? = null
    private var cursorSms: Cursor? = null
    private var arrSMSContacts: ArrayList<Contacts> = ArrayList()
    private var arrWhatsContacts: ArrayList<Contacts> = ArrayList()

    private var spinnerValue: String = ""
    lateinit var adapter: ContactsAdapter

    private var textVal: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        spinnerValue = intent.extras!!.getString("spinnerValue")!!

        toolbar.setTitle(R.string.selectReceiver)
        setSupportActionBar(toolbar)

        getContacts()
    }

    @SuppressLint("WrongConstant", "Recycle")
    private fun getContacts(){
        progressBar.visibility = View.VISIBLE
        cursorSms = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC")

        cursorWhatsApp = contentResolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            arrayOf(ContactsContract.RawContacts._ID, ContactsContract.RawContacts.CONTACT_ID),
            ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
            arrayOf("com.whatsapp"),
            ContactsContract.Contacts.DISPLAY_NAME + " ASC")

        if (cursorWhatsApp != null) {
            if (cursorWhatsApp!!.count > 0) {
                if (cursorWhatsApp!!.moveToFirst()) {
                    do {
                        //whatsappContactId for get Number,Name,Id ect... from  ContactsContract.CommonDataKinds.Phone
                        val whatsappContactId =
                            cursorWhatsApp!!.getString(cursorWhatsApp!!.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID))

                        if (whatsappContactId != null) {
                            //Get Data from ContactsContract.CommonDataKinds.Phone of Specific CONTACT_ID
                            val whatsAppContactCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                arrayOf(
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                                ),
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(whatsappContactId), null
                            )

                            if (whatsAppContactCursor != null) {
                                whatsAppContactCursor.moveToFirst()
                                val id = whatsAppContactCursor.getString(
                                    whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                                )
                                val name = whatsAppContactCursor.getString(
                                    whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                                )
                                val number = whatsAppContactCursor.getString(
                                    whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                )

                                whatsAppContactCursor.close()

                                //Add Number to ArrayList
                                arrWhatsContacts.add(Contacts(name, number))
                            }
                        }
                    } while (cursorWhatsApp!!.moveToNext())
                    cursorWhatsApp!!.close()
                }
            }
        }

        while (cursorSms!!.moveToNext()) {
            val name = cursorSms!!.getString(cursorSms!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = cursorSms!!.getString(cursorSms!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            val contact = Contacts(name, phoneNumber)

            if (arrSMSContacts.size > 0) {
                if (arrSMSContacts[arrSMSContacts.size - 1].getName() != name || !arrSMSContacts[arrSMSContacts.size - 1].getPhone().startsWith(phoneNumber.subSequence(0, 3))){
                    arrSMSContacts.add(contact)
                }
            } else {
                arrSMSContacts.add(contact)
            }
        }
        cursorSms!!.close()

        adapter = if (spinnerValue == "SMS") {
            ContactsAdapter(arrSMSContacts, this, "SMS")
        } else {
            ContactsAdapter(arrWhatsContacts, this, "WhatsApp")
        }
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)
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
