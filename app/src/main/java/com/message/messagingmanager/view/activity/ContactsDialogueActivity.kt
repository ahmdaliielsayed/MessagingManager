package com.message.messagingmanager.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Contact
import com.message.messagingmanager.view.adapter.ContactsDialogueAdapter
import kotlinx.android.synthetic.main.activity_contacts_dialogue.*
import java.util.ArrayList

class ContactsDialogueActivity : AppCompatActivity() {

    private var contactsAdapter: ContactsDialogueAdapter? = null
    private lateinit var contactsArrayList: ArrayList<Contact>

    private lateinit var userId: String
    private lateinit var databaseReferenceContacts: DatabaseReference

    private var groupID: String? = null
    private var groupName:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_dialogue)

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        initComponents()
    }

    private fun initComponents() {
        groupID = intent.getStringExtra("groupID")
        groupName = intent.getStringExtra("groupName")
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        databaseReferenceContacts = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Groups").child(groupID.toString()).child("Contacts")

        txtViewGroupName.text = groupName
        contactsArrayList = ArrayList()
    }

    public override fun onStart() {
        super.onStart()

        databaseReferenceContacts.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                contactsArrayList.clear()

                for (noteSnapshot in dataSnapshot.children) {
                    val contact = noteSnapshot.getValue(Contact::class.java)

                    if (contact!!.getGroupId() == groupID) {
                        contactsArrayList.add(contact)
                    }

                    setAdapter()
                }

                if (contactsArrayList.size <= 0) {
                    linearLayoutFragment.visibility = View.VISIBLE
                    return
                } else {
                    linearLayoutFragment.visibility = View.GONE
                    return
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun setAdapter() {

        val manager = LinearLayoutManager(this)
        recyclerView.layoutManager = manager
        contactsAdapter = ContactsDialogueAdapter(this, groupID.toString())
        recyclerView.adapter = contactsAdapter
        setDataSource()

    }

    private fun setDataSource() {
        contactsAdapter!!.setDataToAdapter(contactsArrayList)
    }
}
