package com.message.messagingmanager.view.activity.messages

import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.message.messagingmanager.R
import com.message.messagingmanager.view.activity.messages.specifiedconversation.SpecifiedMessageActivity
import java.lang.String

class SmsRecyclerAdapter(objectList: List<SmsEntity?>?, listener: RecyclerClickListener?, private var messageActivity : MessagesActivity) :
    BaseRecyclerAdapter<SmsRecyclerAdapter.CustomViewHolder, SmsEntity>(
        objectList as List<SmsEntity>,
        listener!!
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_sms_list_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val entity = getItem(position)

        holder.getContainer()!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(messageActivity, SpecifiedMessageActivity::class.java)
            intent.putExtra("SmsReceiverName", entity.getAddress())
            messageActivity.startActivity(intent)
        })
        holder.getTxtSmsHeader()!!.text = entity.getAddress()
        holder.getTxtIconText()!!.text = if (TextUtils.isEmpty(entity.getAddress())) "A" else String.valueOf(entity.getAddress()!![0])
        holder.getTxtSmsMsg()!!.text = entity.getMsg()
        holder.getTxtTimeStamp()!!.text = Utils.getTimeStamp(entity.getDate()!!)
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var container: ConstraintLayout? = null
        private var txtIconText: TextView? = null
        private var txtSmsHeader: TextView? = null
        private var txtSmsMsg: TextView? = null
        private var txtTimeStamp: TextView? = null

        fun getContainer(): ConstraintLayout? {
            if (container == null) {
                container = itemView.findViewById(R.id.container)
            }
            return container
        }

        fun getTxtIconText(): TextView? {
            if (txtIconText == null) {
                txtIconText = itemView.findViewById(R.id.txtIconText)
            }
            return txtIconText
        }

        fun getTxtSmsHeader(): TextView? {
            if (txtSmsHeader == null) {
                txtSmsHeader = itemView.findViewById(R.id.txtSmsHeader)
            }
            return txtSmsHeader
        }

        fun getTxtSmsMsg(): TextView? {
            if (txtSmsMsg == null) {
                txtSmsMsg = itemView.findViewById(R.id.txtSmsMsg)
            }
            return txtSmsMsg
        }

        fun getTxtTimeStamp(): TextView? {
            if (txtTimeStamp == null) {
                txtTimeStamp = itemView.findViewById(R.id.txtTimeStamp)
            }
            return txtTimeStamp
        }
    }
}