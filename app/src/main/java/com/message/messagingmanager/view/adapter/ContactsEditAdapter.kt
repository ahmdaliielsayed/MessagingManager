package com.message.messagingmanager.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Contacts
import com.message.messagingmanager.view.activity.ContactsEditActivity
import com.message.messagingmanager.view.activity.EditScheduleMessageActivity

class ContactsEditAdapter(private val contactsList: ArrayList<Contacts>, private var contactsEditActivity: ContactsEditActivity,
                          private var smsId: String, private var smsMsg: String, private var smsDate: String,
                          private var smsTime: String, private var smsStatus: String, private var smsType: String,
                          private var userID: String, private var calendar: Long): RecyclerView.Adapter<ContactsEditAdapter.DataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_person,parent,false)
        return DataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        val contact = contactsList[position]

        holder.getCardView()!!.setOnClickListener {
            val intent = Intent(contactsEditActivity, EditScheduleMessageActivity::class.java)
            intent.putExtra("SmsId", smsId)
            intent.putExtra("SmsReceiverName", contact.getName())
            intent.putExtra("SmsReceiverNumber", contact.getPhone())
            intent.putExtra("SmsMsg", smsMsg)
            intent.putExtra("SmsDate", smsDate)
            intent.putExtra("SmsTime", smsTime)
            intent.putExtra("SmsStatus", smsStatus)
            intent.putExtra("SmsType", smsType)
            intent.putExtra("UserID", userID)
            intent.putExtra("calendar", calendar)
            contactsEditActivity.startActivity(intent)
            contactsEditActivity.finish()
        }

        holder.getTxtViewPersonName()!!.text = contact.getName()
        holder.getTxtViewPhoneNumber()!!.text = contact.getPhone()
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var cardView: CardView? = null
        private var txtViewPersonName: TextView? = null
        private var txtViewPhoneNumber: TextView? = null

        fun getCardView(): CardView? {
            if (cardView == null){
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

        fun getTxtViewPhoneNumber(): TextView? {
            if (txtViewPhoneNumber == null) {
                txtViewPhoneNumber = itemView.findViewById(R.id.txtViewPhoneNumber)
            }
            return txtViewPhoneNumber
        }
    }
}