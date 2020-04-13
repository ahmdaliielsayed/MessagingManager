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
import com.message.messagingmanager.model.SIM
import java.util.ArrayList

class SIMAdapter(internal var context: Context) :
    RecyclerView.Adapter<SIMAdapter.DataViewHolder>() {

    private var dataModelList = ArrayList<SIM>()

    private var databaseSIM = FirebaseDatabase.getInstance().getReference("SIMs")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_sim, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        val sim = dataModelList[position]

        holder.getTxtViewSIMName()!!.text = sim.getSimName()
        holder.getTxtViewPrefix()!!.text = sim.getSimPrefix()

        holder.getIvDelete()!!.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.deleteSIM)
            builder.setPositiveButton(R.string.yes) { _, _ ->
                databaseSIM.child(sim.getSimId()).removeValue()
                Toast.makeText(context, R.string.simDeleted, Toast.LENGTH_SHORT).show()
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

    fun setDataToAdapter(dataModelList: ArrayList<SIM>) {
        this.dataModelList = dataModelList
        notifyDataSetChanged()
    }

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var txtViewSIMName: TextView? = null
        private var txtViewPrefix: TextView? = null
        private var ivDelete: ImageView? = null

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
        fun getIvDelete(): ImageView? {
            if (ivDelete == null) {
                ivDelete = itemView.findViewById(R.id.ivDelete)
            }
            return ivDelete
        }
    }
}