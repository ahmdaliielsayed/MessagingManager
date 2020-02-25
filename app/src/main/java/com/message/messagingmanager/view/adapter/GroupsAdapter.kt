package com.message.messagingmanager.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Group
import com.message.messagingmanager.view.activity.GroupsActivity
import com.message.messagingmanager.view.activity.ScheduleGroupMessageActivity

class GroupsAdapter(private val groupsList: ArrayList<Group>, private var groupsActivity: GroupsActivity) :
    RecyclerView.Adapter<GroupsAdapter.DataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_select_group, parent, false)

        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        val group = groupsList[position]

        holder.getTxtViewGroupName()!!.text = group.getGroupName()

        holder.getConstraintLayout()!!.setOnClickListener {
            val openNoteDialogue = Intent(groupsActivity, ScheduleGroupMessageActivity::class.java)
            openNoteDialogue.putExtra("groupID", group.getGroupId())
            openNoteDialogue.putExtra("groupName", group.getGroupName())
            groupsActivity.startActivity(openNoteDialogue)
        }
    }

    override fun getItemCount(): Int {
        return if (groupsList.size > 0) groupsList.size else 0
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var txtViewGroupName: TextView? = null
        private var constraintLayout: ConstraintLayout? = null

        fun getTxtViewGroupName(): TextView? {
            if (txtViewGroupName == null) {
                txtViewGroupName = itemView.findViewById(R.id.txtViewGroupName)
            }
            return txtViewGroupName
        }
        fun getConstraintLayout(): ConstraintLayout? {
            if (constraintLayout == null) {
                constraintLayout = itemView.findViewById(R.id.constraintLayout)
            }
            return constraintLayout
        }
    }
}