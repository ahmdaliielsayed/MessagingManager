package com.message.messagingmanager.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Group
import com.message.messagingmanager.view.adapter.GroupsAdapter
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.activity_contacts.progressBar
import kotlinx.android.synthetic.main.activity_contacts.recyclerView
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.app_bar.*

class GroupsActivity : AppCompatActivity() {

    private var arrGroups: ArrayList<Group> = ArrayList()
    private var databaseReferenceGroups: DatabaseReference = FirebaseDatabase.getInstance().getReference("Groups")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        toolbar.setTitle(R.string.selectGroup)
        setSupportActionBar(toolbar)

        databaseReferenceGroups.keepSynced(true)

        txtViewNoItemAddGroup.setOnClickListener {
            startActivity(Intent(this@GroupsActivity, CreateGroupActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        progressBar.visibility = View.VISIBLE
        databaseReferenceGroups.addValueEventListener(object : ValueEventListener {
            @SuppressLint("WrongConstant")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                arrGroups.clear()

                for (msgSnapshot in dataSnapshot.children) {
                    val group = msgSnapshot.getValue(Group::class.java)

                    if (group!!.getUserID() == FirebaseAuth.getInstance().currentUser!!.uid) {
                        arrGroups.add(group)
                    }
                }

                if (arrGroups.size <= 0) {
                    progressBar.visibility = View.GONE
                    findViewById<LinearLayout>(R.id.linearLayoutFragment).visibility = View.VISIBLE
                } else {
                    findViewById<LinearLayout>(R.id.linearLayoutFragment).visibility = View.GONE

                    val adapter = GroupsAdapter(arrGroups, this@GroupsActivity)
                    recyclerView.layoutManager = LinearLayoutManager(this@GroupsActivity, LinearLayout.VERTICAL, false)
                    recyclerView.adapter = adapter

                    progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
