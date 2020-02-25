package com.message.messagingmanager.ui.upcoming

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Message
import com.message.messagingmanager.view.activity.GroupsActivity
import com.message.messagingmanager.view.activity.ScheduleMessageActivity
import java.util.ArrayList

class UpcomingFragment() : Fragment() {

    private lateinit var upcomingViewModel: UpcomingViewModel
    private var recyclerView: RecyclerView? = null

    private lateinit var databaseReferenceMsg: DatabaseReference

    internal lateinit var upcomingMsgList: ArrayList<Message>

    internal lateinit var view: View

    companion object{
        @SuppressLint("StaticFieldLeak")
        var activiy:Activity= Activity()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view = inflater.inflate(R.layout.fragment_upcoming, container, false)
        recyclerView = view.findViewById(R.id.recyclerUpcoming)

        databaseReferenceMsg = FirebaseDatabase.getInstance().getReference("Messages")
        databaseReferenceMsg.keepSynced(true)
        upcomingMsgList = ArrayList()

        val floatingActionButtonSendPerson = view.findViewById<FloatingActionButton>(R.id.floatingActionButtonSendPerson)
        floatingActionButtonSendPerson.setOnClickListener {
            val intent = Intent(activity, ScheduleMessageActivity::class.java)
            intent.putExtra("SmsReceiverName", "No Name!")
            intent.putExtra("SmsReceiverNumber", "")
            startActivity(intent)
        }

        val floatingActionButtonSendGroup = view.findViewById<FloatingActionButton>(R.id.floatingActionButtonSendGroup)
        floatingActionButtonSendGroup.setOnClickListener {
            startActivity(Intent(activity, GroupsActivity::class.java))
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        databaseReferenceMsg.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                upcomingMsgList.clear()

                for (msgSnapshot in dataSnapshot.children) {
                    val upcomingMsg = msgSnapshot.getValue(Message::class.java)

                    if (upcomingMsg!!.getUserID() == FirebaseAuth.getInstance().currentUser!!.uid) {
                        if (upcomingMsg.getSmsStatus() == "Upcoming") {
                            upcomingMsgList.add(upcomingMsg)
                        }
                    }

//                    setAdapter()
                }

                if (upcomingMsgList.size <= 0) {
                    view.refreshDrawableState()
                    view.findViewById<LinearLayout>(R.id.linearLayoutFragmentUpcoming).visibility = View.VISIBLE
                    view.refreshDrawableState()
                } else {
                    view.refreshDrawableState()
                    view.findViewById<LinearLayout>(R.id.linearLayoutFragmentUpcoming).visibility = View.GONE
                    setAdapter()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun setAdapter() {

        val manager = LinearLayoutManager(activiy)
        recyclerView!!.layoutManager = manager
        upcomingViewModel = UpcomingViewModel(activiy)
        recyclerView!!.adapter = upcomingViewModel
        setDataSource()
    }

    private fun setDataSource() {
        upcomingViewModel.setDataToAdapter(upcomingMsgList)
    }
}