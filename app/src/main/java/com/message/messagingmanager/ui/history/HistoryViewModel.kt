package com.message.messagingmanager.ui.history

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Message

class HistoryViewModel(internal var context: Context) :
    RecyclerView.Adapter<HistoryViewModel.DataViewHolder>() {

    private var dataModelList = ArrayList<Message>()

    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid
    private var databaseMsg = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Messages")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_history_person, parent, false)

        return DataViewHolder(itemView)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        val msg = dataModelList[position]

        holder.getTxtViewDateHistoryPerson()!!.text = msg.getSmsDate()
        holder.getTxtViewTimeHistoryPerson()!!.text = msg.getSmsTime()
        holder.getTxtViewStatusHistoryPerson()!!.text = msg.getSmsStatus()
        holder.getTxtViewHistoryPersonName()!!.text = msg.getSmsReceiverName()
        holder.getTxtViewHistoryPhoneNumber()!!.text = msg.getSmsReceiverNumber()
        holder.getTxtViewHistoryMessage()!!.text = msg.getSmsMsg()

        if (msg.getSmsType() == "SMS"){
            holder.getImgViewMsgType()!!.setImageResource(R.drawable.sms)
        } else {
            holder.getImgViewMsgType()!!.setImageResource(R.drawable.whatsapp)
        }

        holder.getConstraintLayout()!!.setOnClickListener { view ->
            if (holder.getConstraintLayoutDetails()!!.visibility == View.VISIBLE) {
                holder.getConstraintLayoutDetails()!!.visibility = View.GONE
            } else {
                holder.getConstraintLayoutDetails()!!.visibility = View.VISIBLE

                val animation = AnimationUtils.loadAnimation(view.context, R.anim.open_card)
                animation.duration = 500
                holder.getConstraintLayoutDetails()!!.animation = animation
                holder.getConstraintLayoutDetails()!!.animate()
                animation.start()
            }
        }

        holder.getImageButtonPopUp()!!.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.deleteMsg)
            builder.setPositiveButton(R.string.yes) { _, _ ->
                databaseMsg.child(msg.getSmsId()).removeValue()

                Toast.makeText(context, R.string.confirmMsgDeletion, Toast.LENGTH_SHORT).show()

                dataModelList.remove(msg)
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

    fun setDataToAdapter(dataModelList: ArrayList<Message>) {
        this.dataModelList = dataModelList
        notifyDataSetChanged()
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var txtViewDateHistoryPerson: TextView? = null
        private var txtViewTimeHistoryPerson: TextView? = null
        private var txtViewStatusHistoryPerson: TextView? = null
        private var txtViewHistoryPersonName: TextView? = null
        private var txtViewHistoryPhoneNumber: TextView? = null
        private var txtViewHistoryMessage: TextView? = null
        private var imgViewMsgType: ImageView? = null
        private var constraintLayout: ConstraintLayout? = null
        private var imageButtonPopUp: ImageButton? = null
        private var constraintLayoutDetails: ConstraintLayout? = null

        fun getTxtViewDateHistoryPerson(): TextView? {
            if (txtViewDateHistoryPerson == null) {
                txtViewDateHistoryPerson = itemView.findViewById(R.id.txtViewDateHistoryPerson)
            }
            return txtViewDateHistoryPerson
        }
        fun getTxtViewTimeHistoryPerson(): TextView? {
            if (txtViewTimeHistoryPerson == null) {
                txtViewTimeHistoryPerson = itemView.findViewById(R.id.txtViewTimeHistoryPerson)
            }
            return txtViewTimeHistoryPerson
        }
        fun getTxtViewStatusHistoryPerson(): TextView? {
            if (txtViewStatusHistoryPerson == null) {
                txtViewStatusHistoryPerson = itemView.findViewById(R.id.txtViewStatusHistoryPerson)
            }
            return txtViewStatusHistoryPerson
        }
        fun getTxtViewHistoryPersonName(): TextView? {
            if (txtViewHistoryPersonName == null) {
                txtViewHistoryPersonName = itemView.findViewById(R.id.txtViewHistoryPersonName)
            }
            return txtViewHistoryPersonName
        }
        fun getTxtViewHistoryPhoneNumber(): TextView? {
            if (txtViewHistoryPhoneNumber == null) {
                txtViewHistoryPhoneNumber = itemView.findViewById(R.id.txtViewHistoryPhoneNumber)
            }
            return txtViewHistoryPhoneNumber
        }
        fun getTxtViewHistoryMessage(): TextView? {
            if (txtViewHistoryMessage == null) {
                txtViewHistoryMessage = itemView.findViewById(R.id.txtViewHistoryMessage)
            }
            return txtViewHistoryMessage
        }
        fun getImgViewMsgType(): ImageView? {
            if (imgViewMsgType == null) {
                imgViewMsgType = itemView.findViewById(R.id.imgViewMsgType)
            }
            return imgViewMsgType
        }
        fun getConstraintLayout(): ConstraintLayout? {
            if (constraintLayout == null) {
                constraintLayout = itemView.findViewById(R.id.constraintLayout)
            }
            return constraintLayout
        }
        fun getImageButtonPopUp(): ImageButton? {
            if (imageButtonPopUp == null) {
                imageButtonPopUp = itemView.findViewById(R.id.imageButtonPopUp)
            }
            return imageButtonPopUp
        }
        fun getConstraintLayoutDetails(): ConstraintLayout? {
            if (constraintLayoutDetails == null) {
                constraintLayoutDetails = itemView.findViewById(R.id.constraintLayoutDetails)
            }
            return constraintLayoutDetails
        }
    }
}