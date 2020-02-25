package com.message.messagingmanager.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.message.messagingmanager.HomeActivity
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Contact
import com.message.messagingmanager.model.Contacts
import com.message.messagingmanager.view.activity.ContactsGroupActivity
import kotlinx.android.synthetic.main.activity_contacts_group.*

class ContactsGroupAdapter(
    private val contactsList: ArrayList<Contacts>, private var contactsGroupActivity: ContactsGroupActivity,
    private val groupId: String) : RecyclerView.Adapter<ContactsGroupAdapter.DataViewHolder>() {

    private var groupContactList: ArrayList<Contacts> = ArrayList()
    private var deleteGroupContactList: ArrayList<Contact> = ArrayList()
    private var databaseReferenceContact: DatabaseReference = FirebaseDatabase.getInstance().getReference("Contacts")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_select_person, parent, false)
        return DataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onViewRecycled(holder: DataViewHolder) {
        holder.getImgViewSelect()!!.isPressed = false
        super.onViewRecycled(holder)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        databaseReferenceContact.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (contactSnapshot in dataSnapshot.children) {
                    val contact = contactSnapshot.getValue(Contact::class.java)

                    if (contact!!.getGroupId() == groupId) {
                        deleteGroupContactList.add(contact)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        val contact = contactsList[position]

        holder.getCardView()!!.setOnClickListener {
            contactsList[holder.adapterPosition]
            if (groupContactList.contains(contact)) {
                holder.getImgViewSelect()!!.visibility = View.INVISIBLE
                groupContactList.remove(contact)
            } else {
                holder.getImgViewSelect()!!.visibility = View.VISIBLE
                groupContactList.add(contact)
            }
        }

        holder.getTxtViewPersonName()!!.text = contact.getName()
        holder.getTxtViewPhoneNumber()!!.text = contact.getPhone()

        contactsGroupActivity.btnDone.setOnClickListener {
            when {
                groupContactList.isEmpty() -> {
                    Toast.makeText(contactsGroupActivity, "Can't create Empty group!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    if (deleteGroupContactList.size > 0){
                        for (i in 0 until deleteGroupContactList.size) {
                            for (j in 0 until groupContactList.size){
                                if (groupContactList[j].getName() == deleteGroupContactList[i].getContactName()){
                                    databaseReferenceContact.child(deleteGroupContactList[i].getContactId()).removeValue()
                                }
                            }
                        }
                    }

                    for (item in groupContactList.indices) {
                        val receiverNumber: String
                        val contactId = databaseReferenceContact.push().key.toString()
                        @Suppress("NAME_SHADOWING")

                        receiverNumber = if (groupContactList[item].getPhone().trim().length == 11) {
                            "+2" + groupContactList[item].getPhone()
                        } else if (groupContactList[item].getPhone().trim().length == 13 && groupContactList[item].getPhone().trim().contains(" ")) {
                            "+2" + groupContactList[item].getPhone()
                        } else {
                            groupContactList[item].getPhone().trim()
                        }

                        val contact = Contact(contactId, groupContactList[item].getName(), receiverNumber, groupId)
                        databaseReferenceContact.child(contactId).setValue(contact)
                    }

                    Toast.makeText(
                        contactsGroupActivity,
                        "Group contacts created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    contactsGroupActivity.startActivity(
                        Intent(
                            contactsGroupActivity,
                            HomeActivity::class.java
                        )
                    )
                    contactsGroupActivity.finish()
                }
            }
        }
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var cardView: CardView? = null
        private var txtViewPersonName: TextView? = null
        private var imgViewSelect: ImageView? = null
        private var txtViewPhoneNumber: TextView? = null

        fun getCardView(): CardView? {
            if (cardView == null) {
                cardView = itemView.findViewById(R.id.cardView)
            }
            return cardView
        }

        fun getTxtViewPersonName(): TextView? {
            if (txtViewPersonName == null) {
                txtViewPersonName = itemView.findViewById(R.id.txtViewPersonName)
            }
            return txtViewPersonName
        }

        fun getImgViewSelect(): ImageView? {
            if (imgViewSelect == null) {
                imgViewSelect = itemView.findViewById(R.id.imgViewSelect)
            }
            return imgViewSelect
        }

        fun getTxtViewPhoneNumber(): TextView? {
            if (txtViewPhoneNumber == null) {
                txtViewPhoneNumber = itemView.findViewById(R.id.txtViewPhoneNumber)
            }
            return txtViewPhoneNumber
        }
    }


}