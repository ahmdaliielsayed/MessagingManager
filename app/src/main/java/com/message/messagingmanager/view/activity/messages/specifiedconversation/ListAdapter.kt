package com.message.messagingmanager.view.activity.messages.specifiedconversation

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.message.messagingmanager.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListAdapter(private val myDatabase: ArrayList<Sms>) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_sms_message, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myDatabase.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.getTxtMsgBody()!!.text = myDatabase[position].body
        holder.getTxtDate()!!.text = getTimeStamp(myDatabase[position].date)
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeStamp(timestamp: String): String? {
        return if (!TextUtils.isEmpty(timestamp)) {
            val datetime = timestamp.toLong()
            val date = Date(datetime)
            val formatter: DateFormat = SimpleDateFormat("dd/MM/YYYY HH:mm")
            formatter.format(date)
        } else {
            "--"
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var txtMsgBody: TextView? = null
        private var txtDate: TextView? = null

        fun getTxtMsgBody(): TextView? {
            if (txtMsgBody == null) {
                txtMsgBody = itemView.findViewById(R.id.txtSmsMsg)
            }
            return txtMsgBody
        }

        fun getTxtDate(): TextView? {
            if (txtDate == null) {
                txtDate = itemView.findViewById(R.id.txtTimeStamp)
            }
            return txtDate
        }
    }
}