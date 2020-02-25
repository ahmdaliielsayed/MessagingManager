package com.message.messagingmanager.ui.upcoming

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Message
import com.message.messagingmanager.view.activity.EditScheduleMessageActivity
import com.message.messagingmanager.view.receiver.AlertReceiver
import java.util.*
import kotlin.collections.ArrayList

class UpcomingViewModel(internal var context: Activity) :
    RecyclerView.Adapter<UpcomingViewModel.DataViewHolder>() {

    private  var dataModelList:ArrayList<Message>? = ArrayList()

    private var databaseMsg = FirebaseDatabase.getInstance().getReference("Messages")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_upcoming_person, parent, false)

        return DataViewHolder(itemView)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        val msg = dataModelList!![position]

        holder.getTxtViewDateUpcomingPerson()!!.text = msg.getSmsDate()
        holder.getTxtViewTimeUpcomingPerson()!!.text = msg.getSmsTime()
        holder.getTxtViewStatusUpcomingPerson()!!.text = msg.getSmsStatus()
        holder.getTxtViewUpcomingPersonName()!!.text = msg.getSmsReceiverName()
        holder.getTxtViewUpcomingPhoneNumber()!!.text = msg.getSmsReceiverNumber()
        holder.getTxtViewUpcomingMessage()!!.text = msg.getSmsMsg()

        if (msg.getSmsType() == "SMS"){
            holder.getImgViewMsgType()!!.setImageResource(R.drawable.sms)
        } else {
            holder.getImgViewMsgType()!!.setImageResource(R.drawable.whatsapp)
        }

        holder.getImageViewExpandCard()!!.setOnClickListener { view ->
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
            val popupMenu = PopupMenu(context, holder.getImageButtonPopUp())
            popupMenu.menuInflater.inflate(R.menu.menu_upcoming_msg_person, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.item_edit -> {
                        val intent = Intent(context, EditScheduleMessageActivity::class.java)
                        intent.putExtra("SmsId", msg.getSmsId())
                        intent.putExtra("SmsReceiverName", msg.getSmsReceiverName())
                        intent.putExtra("SmsReceiverNumber", msg.getSmsReceiverNumber())
                        intent.putExtra("SmsMsg", msg.getSmsMsg())
                        intent.putExtra("SmsDate", msg.getSmsDate())
                        intent.putExtra("SmsTime", msg.getSmsTime())
                        intent.putExtra("SmsStatus", msg.getSmsStatus())
                        intent.putExtra("SmsType", msg.getSmsType())
                        intent.putExtra("UserID", msg.getUserID())
                        intent.putExtra("calendar", msg.getSmsCalender())
                        context.startActivity(intent)
                    }
                    R.id.item_delete -> {
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("R U sure to delete this message?")
                        builder.setPositiveButton("Yes") { _, _ ->
                            databaseMsg.child(msg.getSmsId()).removeValue()

                            val alarmManager =
                                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                            val iDelete = Intent(context, AlertReceiver::class.java)
                            iDelete.putExtra("SmsId", msg.getSmsId())
                            iDelete.putExtra("SmsReceiverName", msg.getSmsReceiverName())
                            iDelete.putExtra("SmsReceiverNumber", msg.getSmsReceiverNumber())
                            iDelete.putExtra("SmsMsg", msg.getSmsMsg())
                            iDelete.putExtra("SmsDate", msg.getSmsDate())
                            iDelete.putExtra("SmsTime", msg.getSmsTime())
                            iDelete.putExtra("SmsStatus", msg.getSmsStatus())
                            iDelete.putExtra("SmsType", msg.getSmsType())
                            iDelete.putExtra("UserID", msg.getUserID())
                            iDelete.putExtra("calendar", msg.getSmsCalender())

                            val pendingIntent = PendingIntent.getBroadcast(context,
                                msg.getSmsId().hashCode(), iDelete,
                                PendingIntent.FLAG_UPDATE_CURRENT)
                            alarmManager.cancel(pendingIntent)

                            Toast.makeText(context, "Msg Deleted Successfully!",
                                Toast.LENGTH_SHORT).show()

                            dataModelList!!.remove(msg)
                            setDataToAdapter(dataModelList!!)
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

                    R.id.item_send -> {

                        val alarmManager =
                            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                        val iDone = Intent(context, AlertReceiver::class.java)
                        iDone.putExtra("SmsId", msg.getSmsId())
                        iDone.putExtra("SmsReceiverName", msg.getSmsReceiverName())
                        iDone.putExtra("SmsReceiverNumber", msg.getSmsReceiverNumber())
                        iDone.putExtra("SmsMsg", msg.getSmsMsg())
                        iDone.putExtra("SmsDate", msg.getSmsDate())
                        iDone.putExtra("SmsTime", msg.getSmsTime())
                        iDone.putExtra("SmsStatus", msg.getSmsStatus())
                        iDone.putExtra("SmsType", msg.getSmsType())
                        iDone.putExtra("UserID", msg.getUserID())
                        iDone.putExtra("calendar", msg.getSmsCalender())

                        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context,
                            msg.getSmsId().hashCode(), iDone, PendingIntent.FLAG_UPDATE_CURRENT)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().time.time, pendingIntent)
                        } else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().time.time, pendingIntent)
                        }

                        Toast.makeText(context, "Message scheduled Successfully to now!",
                            Toast.LENGTH_SHORT).show()

                        dataModelList!!.remove(msg)
                        setDataToAdapter(dataModelList!!)
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int {
        return if (dataModelList == null) 0 else dataModelList!!.size
    }

    fun setDataToAdapter(dataModelList: ArrayList<Message>) {
        this.dataModelList = dataModelList
        notifyDataSetChanged()
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var txtViewDateUpcomingPerson: TextView? = null
        private var txtViewTimeUpcomingPerson: TextView? = null
        private var txtViewStatusUpcomingPerson: TextView? = null
        private var txtViewUpcomingPersonName: TextView? = null
        private var txtViewUpcomingPhoneNumber: TextView? = null
        private var txtViewUpcomingMessage: TextView? = null
        private var imgViewMsgType: ImageView? = null
        private var constraintLayoutExpandCard: ConstraintLayout? = null
        private var imageButtonPopUp: ImageButton? = null
        private var constraintLayoutDetails: ConstraintLayout? = null

        fun getTxtViewDateUpcomingPerson(): TextView? {
            if (txtViewDateUpcomingPerson == null) {
                txtViewDateUpcomingPerson = itemView.findViewById(R.id.txtViewDateUpcomingPerson)
            }
            return txtViewDateUpcomingPerson
        }
        fun getTxtViewTimeUpcomingPerson(): TextView? {
            if (txtViewTimeUpcomingPerson == null) {
                txtViewTimeUpcomingPerson = itemView.findViewById(R.id.txtViewTimeUpcomingPerson)
            }
            return txtViewTimeUpcomingPerson
        }
        fun getTxtViewStatusUpcomingPerson(): TextView? {
            if (txtViewStatusUpcomingPerson == null) {
                txtViewStatusUpcomingPerson = itemView.findViewById(R.id.txtViewStatusUpcomingPerson)
            }
            return txtViewStatusUpcomingPerson
        }
        fun getTxtViewUpcomingPersonName(): TextView? {
            if (txtViewUpcomingPersonName == null) {
                txtViewUpcomingPersonName = itemView.findViewById(R.id.txtViewUpcomingPersonName)
            }
            return txtViewUpcomingPersonName
        }
        fun getTxtViewUpcomingPhoneNumber(): TextView? {
            if (txtViewUpcomingPhoneNumber == null) {
                txtViewUpcomingPhoneNumber = itemView.findViewById(R.id.txtViewUpcomingPhoneNumber)
            }
            return txtViewUpcomingPhoneNumber
        }
        fun getTxtViewUpcomingMessage(): TextView? {
            if (txtViewUpcomingMessage == null) {
                txtViewUpcomingMessage = itemView.findViewById(R.id.txtViewUpcomingMessage)
            }
            return txtViewUpcomingMessage
        }
        fun getImgViewMsgType(): ImageView? {
            if (imgViewMsgType == null) {
                imgViewMsgType = itemView.findViewById(R.id.imgViewMsgType)
            }
            return imgViewMsgType
        }
        fun getImageViewExpandCard(): ConstraintLayout? {
            if (constraintLayoutExpandCard == null) {
                constraintLayoutExpandCard = itemView.findViewById(R.id.constraintLayout)
            }
            return constraintLayoutExpandCard
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