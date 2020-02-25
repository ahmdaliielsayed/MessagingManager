package com.message.messagingmanager.ui.history

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Message

class HistoryFragment() : Fragment() {

    private lateinit var historyViewModel: HistoryViewModel
    private var recyclerView: RecyclerView? = null

    private lateinit var databaseReferenceMsg: DatabaseReference

    internal lateinit var historyMsgList: ArrayList<Message>
    internal lateinit var view: View
    companion object{
        @SuppressLint("StaticFieldLeak")
        var activiy: Activity = Activity()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view = inflater.inflate(R.layout.fragment_history, container, false)
        recyclerView = view.findViewById(R.id.recyclerHistory)

        databaseReferenceMsg = FirebaseDatabase.getInstance().getReference("Messages")
        historyMsgList = ArrayList()

        return view
    }

    override fun onStart() {
        super.onStart()

        databaseReferenceMsg.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                historyMsgList.clear()

                for (msgSnapshot in dataSnapshot.children) {
                    val historyMsg = msgSnapshot.getValue(Message::class.java)

                    if (historyMsg!!.getUserID() == FirebaseAuth.getInstance().currentUser!!.uid) {
                        if (historyMsg.getSmsStatus() != "Upcoming") {
                            historyMsgList.add(historyMsg)
                        }
                    }

                    setAdapter()
                }

                if (historyMsgList.size <= 0) {
                    view.refreshDrawableState()
                    view.findViewById<LinearLayout>(R.id.linearLayoutFragmentHistory).visibility = View.VISIBLE
                    view.refreshDrawableState()
                } else {
                    view.refreshDrawableState()
                    view.findViewById<LinearLayout>(R.id.linearLayoutFragmentHistory).visibility = View.GONE
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
        historyViewModel = HistoryViewModel(activiy)
        recyclerView!!.adapter = historyViewModel
        setDataSource()
    }

    private fun setDataSource() {
        historyViewModel.setDataToAdapter(historyMsgList)
    }
}