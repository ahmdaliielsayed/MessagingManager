package com.message.messagingmanager.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.message.messagingmanager.R
import com.message.messagingmanager.model.SIM
import com.message.messagingmanager.view.adapter.SelectSIMAdapter
import kotlinx.android.synthetic.main.activity_networks.*
import kotlinx.android.synthetic.main.activity_networks.recyclerView
import kotlinx.android.synthetic.main.activity_select_sim.*
import kotlinx.android.synthetic.main.app_bar.*
import java.util.ArrayList

class SelectSIMActivity : AppCompatActivity() {

    private var simsAdapter: SelectSIMAdapter? = null
    private var simsArrayList: ArrayList<SIM> = ArrayList()

    private var databaseSIMs: DatabaseReference = FirebaseDatabase.getInstance().getReference("SIMs")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_sim)

        toolbar.title = "Select Network(SIM)"
        setSupportActionBar(toolbar)

        txtViewNoItem.setOnClickListener {
            startActivity(Intent(this@SelectSIMActivity, NetworksActivity::class.java))
        }
    }

    public override fun onStart() {
        super.onStart()

        databaseSIMs.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                simsArrayList.clear()

                for (noteSnapshot in dataSnapshot.children) {
                    val sim = noteSnapshot.getValue(SIM::class.java)

                    if (sim!!.getUserId() == FirebaseAuth.getInstance().currentUser!!.uid) {
                        simsArrayList.add(sim)
                    }

                    setAdapter()
                }

                if (simsArrayList.size <= 0) {
                    findViewById<LinearLayout>(R.id.linearLayoutFragmentUpcoming).visibility = View.VISIBLE
                    return
                } else {
                    findViewById<LinearLayout>(R.id.linearLayoutFragmentUpcoming).visibility = View.GONE
                    return
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
    private fun setAdapter() {

        val manager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = manager
        simsAdapter = SelectSIMAdapter(this)
        recyclerView!!.adapter = simsAdapter
        setDataSource()
    }
    private fun setDataSource() {
        simsAdapter!!.setDataToAdapter(simsArrayList)
    }
}
