package com.message.messagingmanager.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.message.messagingmanager.R
import com.message.messagingmanager.model.SIM
import com.message.messagingmanager.view.adapter.SIMAdapter
import com.message.messagingmanager.viewmodel.NetworksViewModel
import kotlinx.android.synthetic.main.activity_networks.*
import kotlinx.android.synthetic.main.app_bar.*
import java.util.ArrayList

class NetworksActivity : AppCompatActivity() {

    private var simsAdapter: SIMAdapter? = null
    private var simsArrayList: ArrayList<SIM>? = null

    private lateinit var databaseSIMs: DatabaseReference

    private lateinit var networksViewModel: NetworksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_networks)

        toolbar.title = "Configure Networks"
        setSupportActionBar(toolbar)

        networksViewModel = ViewModelProviders.of(this, NetworksViewModelFactory(this@NetworksActivity))
            .get(NetworksViewModel::class.java)

        databaseSIMs = FirebaseDatabase.getInstance().getReference("SIMs")
        databaseSIMs.keepSynced(true)

        btnAddPlus.setOnClickListener {
            when {
                editTxtAddNetwork.text.toString().trim() == "" -> {
                    editTxtAddNetwork.error = "Network(SIM) Name required!"             // getText(R.string.emptyNote)
                    editTxtAddNetwork.requestFocus()
                }
                editTxtAddPrefix.text.toString().trim() == "" -> {
                    editTxtAddPrefix.error = "Prefix of Network(SIM) required!"
                    editTxtAddPrefix.requestFocus()
                }
                !editTxtAddPrefix.text.toString().startsWith("+") -> {
                    editTxtAddPrefix.error = "Prefix of Network(SIM) should starts with sign (+)!"
                    editTxtAddPrefix.requestFocus()
                }
                editTxtAddPrefix.text.toString().trim().length < 2 -> {
                    editTxtAddPrefix.error = "Prefix of Network(SIM) should be at least 2 digits!\nbased on your country, type the prefix of the network!"
                    editTxtAddPrefix.requestFocus()
                }
                else -> {
                    networksViewModel.addSIMViewModel(editTxtAddNetwork.text.toString().trim(), editTxtAddPrefix.text.toString().trim())
                }
            }
        }

        simsArrayList = ArrayList()
    }

    fun setMsgToast(msg: String) {
        editTxtAddNetwork.setText("")
        editTxtAddPrefix.setText("")

        Toast.makeText(this@NetworksActivity, msg, Toast.LENGTH_SHORT).show()
    }

    public override fun onStart() {
        super.onStart()

        databaseSIMs.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                simsArrayList!!.clear()

                for (noteSnapshot in dataSnapshot.children) {
                    val sim = noteSnapshot.getValue(SIM::class.java)

                    if (sim!!.getUserId() == FirebaseAuth.getInstance().currentUser!!.uid) {
                        simsArrayList!!.add(sim)
                    }

                    setAdapter()
                }

                if (simsArrayList!!.size <= 0) {
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
        simsAdapter = SIMAdapter(this)
        recyclerView!!.adapter = simsAdapter
        setDataSource()
    }
    private fun setDataSource() {
        simsAdapter!!.setDataToAdapter(simsArrayList!!)
    }

    inner class NetworksViewModelFactory(private val networksActivity: NetworksActivity) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NetworksViewModel(networksActivity) as T
        }
    }
}
