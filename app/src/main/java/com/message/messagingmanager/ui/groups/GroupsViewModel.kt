package com.message.messagingmanager.ui.groups

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Contact
import com.message.messagingmanager.model.Group
import com.message.messagingmanager.view.activity.ContactsDialogueActivity
import com.message.messagingmanager.view.activity.ContactsGroupActivity

class GroupsViewModel(internal var context: Activity) :
    RecyclerView.Adapter<GroupsViewModel.DataViewHolder>() {

    private var dataModelList = ArrayList<Group>()

    private var databaseGroup = FirebaseDatabase.getInstance().getReference("Groups")
    internal var databaseContacts = FirebaseDatabase.getInstance().getReference("Contacts")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_group, parent, false)

        return DataViewHolder(itemView)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        val group = dataModelList[position]

        holder.getTxtViewGroupName()!!.text = group.getGroupName()

        holder.getConstraintLayout()!!.setOnClickListener {
            val openNoteDialogue = Intent(context, ContactsDialogueActivity::class.java)
            openNoteDialogue.putExtra("groupID", group.getGroupId())
            openNoteDialogue.putExtra("groupName", group.getGroupName())
            context.startActivity(openNoteDialogue)
        }

        holder.getImgViewAddToGroup()!!.setOnClickListener {
            val intent = Intent(context, ContactsGroupActivity::class.java)
            intent.putExtra("groupId", group.getGroupId())
            context.startActivity(intent)
        }

        holder.getImgViewDeleteGroup()!!.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.deleteGroup)
            builder.setPositiveButton(R.string.yes) { _, _ ->
                databaseGroup.child(group.getGroupId()).removeValue()

                // delete related contacts
                databaseContacts.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (noteSnapshot in dataSnapshot.children) {
                            val contacts = noteSnapshot.getValue(Contact::class.java)

                            if (contacts!!.getGroupId() == group.getGroupId()) {
                                databaseContacts.child(contacts.getContactId()).removeValue()
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })

                Toast.makeText(context, R.string.confirmGroupDeletion, Toast.LENGTH_SHORT).show()

                dataModelList.remove(group)
                setDataToAdapter(dataModelList)
            }
            builder.setNegativeButton(R.string.no) { dialogInterface, _ -> dialogInterface.cancel() }

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

    fun setDataToAdapter(dataModelList: ArrayList<Group>) {
        this.dataModelList = dataModelList
        notifyDataSetChanged()
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var txtViewGroupName: TextView? = null
        private var imgViewAddToGroup: ImageView? = null
        private var imgViewDeleteGroup: ImageView? = null
        private var constraintLayout: ConstraintLayout? = null

        fun getTxtViewGroupName(): TextView? {
            if (txtViewGroupName == null) {
                txtViewGroupName = itemView.findViewById(R.id.txtViewGroupName)
            }
            return txtViewGroupName
        }
        fun getImgViewAddToGroup(): ImageView? {
            if (imgViewAddToGroup == null) {
                imgViewAddToGroup = itemView.findViewById(R.id.imgViewAddToGroup)
            }
            return imgViewAddToGroup
        }
        fun getImgViewDeleteGroup(): ImageView? {
            if (imgViewDeleteGroup == null) {
                imgViewDeleteGroup = itemView.findViewById(R.id.imgViewDeleteGroup)
            }
            return imgViewDeleteGroup
        }
        fun getConstraintLayout(): ConstraintLayout? {
            if (constraintLayout == null) {
                constraintLayout = itemView.findViewById(R.id.constraintLayout)
            }
            return constraintLayout
        }
    }
}