package com.message.messagingmanager.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.message.messagingmanager.R
import com.message.messagingmanager.model.SIM
import com.message.messagingmanager.view.activity.ScheduleNetworkMessagesActivity
import java.util.ArrayList

class SelectSIMAdapter(internal var context: Context) : RecyclerView.Adapter<SelectSIMAdapter.DataViewHolder>() {

    private var dataModelList = ArrayList<SIM>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_select_sim, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        val sim = dataModelList[position]

        holder.getTxtViewSIMName()!!.text = sim.getSimName()
        holder.getTxtViewPrefix()!!.text = sim.getSimPrefix()

        holder.getConstraintLayout()!!.setOnClickListener {
            val intent = Intent(context, ScheduleNetworkMessagesActivity::class.java)
            intent.putExtra("SIMName", holder.getTxtViewSIMName()!!.text)
            intent.putExtra("SIMPrefix", holder.getTxtViewPrefix()!!.text)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return if (dataModelList.size > 0) dataModelList.size else 0
    }

    fun setDataToAdapter(dataModelList: ArrayList<SIM>) {
        this.dataModelList = dataModelList
        notifyDataSetChanged()
    }

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var txtViewSIMName: TextView? = null
        private var txtViewPrefix: TextView? = null
        private var constraintLayout: ConstraintLayout? = null

        fun getTxtViewSIMName(): TextView? {
            if (txtViewSIMName == null) {
                txtViewSIMName = itemView.findViewById(R.id.txtViewSIMName)
            }
            return txtViewSIMName
        }
        fun getTxtViewPrefix(): TextView? {
            if (txtViewPrefix == null) {
                txtViewPrefix = itemView.findViewById(R.id.txtViewPrefix)
            }
            return txtViewPrefix
        }
        fun getConstraintLayout(): ConstraintLayout? {
            if (constraintLayout == null) {
                constraintLayout = itemView.findViewById(R.id.constraintLayout)
            }
            return constraintLayout
        }
    }
}