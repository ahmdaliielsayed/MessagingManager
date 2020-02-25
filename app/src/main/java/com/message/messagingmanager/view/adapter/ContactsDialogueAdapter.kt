package com.message.messagingmanager.view.adapter

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Contact
import java.util.ArrayList

class ContactsDialogueAdapter(internal var context: Context) :
    RecyclerView.Adapter<ContactsDialogueAdapter.DataViewHolder>() {

    private var dataModelList = ArrayList<Contact>()

    private var databaseReferenceContacts = FirebaseDatabase.getInstance().getReference("Contacts")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_contact_dialogue, parent, false)
        return DataViewHolder(itemView)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        val contact = dataModelList[position]

        holder.getTxtViewPersonNameDialogue()!!.text = contact.getContactName()
        holder.getTxtViewPhoneNumberDialogue()!!.text = contact.getContactNumber()

        holder.getIvDelete()!!.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("R Y Sure To Delete This Person?")
            builder.setPositiveButton("Yes") { _, _ ->
                databaseReferenceContacts.child(contact.getContactId()).removeValue()
                Toast.makeText(context, "Person Deleted Successfully", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("No") { dialogInterface, _ -> dialogInterface.cancel() }

            val alertDialog = builder.create()
            if (Build.VERSION.SDK_INT >= 26) {
                alertDialog.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL)
            } else {
                alertDialog.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            }
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return if (dataModelList.size > 0) dataModelList.size else 0
    }

    fun setDataToAdapter(dataModelList: ArrayList<Contact>) {
        this.dataModelList = dataModelList
        notifyDataSetChanged()
    }

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var txtViewPersonNameDialogue: TextView? = null
        private var txtViewPhoneNumberDialogue: TextView? = null
        private var ivDelete: ImageView? = null

        fun getTxtViewPersonNameDialogue(): TextView? {
            if (txtViewPersonNameDialogue == null) {
                txtViewPersonNameDialogue = itemView.findViewById(R.id.txtViewPersonNameDialogue)
            }
            return txtViewPersonNameDialogue
        }

        fun getTxtViewPhoneNumberDialogue(): TextView? {
            if (txtViewPhoneNumberDialogue == null) {
                txtViewPhoneNumberDialogue = itemView.findViewById(R.id.txtViewPhoneNumberDialogue)
            }
            return txtViewPhoneNumberDialogue
        }

        fun getIvDelete(): ImageView? {
            if (ivDelete == null) {
                ivDelete = itemView.findViewById(R.id.ivDelete)
            }
            return ivDelete
        }
    }
}