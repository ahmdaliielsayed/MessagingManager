package com.message.messagingmanager.view.activity.messages

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.message.messagingmanager.R
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.app_bar.*
import java.util.ArrayList

class MessagesActivity : AppCompatActivity(), SmsReceiver.SmsListener {

    val REQUEST_DEFAULT_APP = 1
    val MY_PERMISSIONS_REQUEST_SMS = 2

    private var counterReadSms = 0

    private var adapter: SmsRecyclerAdapter? = null
    private var mAdapterList: MutableList<SmsEntity> = ArrayList()

    private val PERMISSIONS = arrayOf(
        Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_MMS,
        Manifest.permission.RECEIVE_WAP_PUSH
    )
    private lateinit var smsViewModel: SmsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        toolbar.setTitle(R.string.messages)
        setSupportActionBar(toolbar)

        SmsReceiver.setListener(this)

        // to make this app as default app for SMS messages
        val setSmsAppIntent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
        setSmsAppIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
        startActivityForResult(setSmsAppIntent, REQUEST_DEFAULT_APP)

        // permissions I need while working
//        val permissionCheck: Int = ContextCompat.checkSelfPermission(this@MessagesActivity, Manifest.permission.READ_SMS)
//        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
//            setUpView()
//        } else {
//            ActivityCompat.requestPermissions(this@MessagesActivity, arrayOf(Manifest.permission.READ_CONTACTS), MY_PERMISSIONS_REQUEST_SMS)
//        }
    }

    private fun setUpView() {
//        val fab = findViewById<FloatingActionButton>(R.id.create_sms_btn)
        val contentRecyclerView = findViewById<RecyclerView>(R.id.content_recycler_view)
//        fab.setOnClickListener {
//            val intent = Intent(this@SmsHomeActivity, SmsComposeActivity::class.java)
//            startActivity(intent)
//        }
        contentRecyclerView.layoutManager = LinearLayoutManager(this)
        contentRecyclerView.setHasFixedSize(true)
        adapter = SmsRecyclerAdapter(mAdapterList, object :
            BaseRecyclerAdapter.RecyclerClickListener {
            override fun onClickAction(view: View?) {}
        }, this)
        contentRecyclerView.adapter = adapter
    }

    fun initViewHolder() {
        smsViewModel = ViewModelProviders.of(this).get(SmsViewModel::class.java)
        smsViewModel.getAllSms().observe(this, observer)
    }

    private var observer: Observer<List<SmsEntity?>?> = Observer<List<SmsEntity?>?> { smsEntityList ->
        mAdapterList.clear()
        mAdapterList.addAll(smsEntityList as Collection<SmsEntity>)
        adapter!!.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DEFAULT_APP  && resultCode  == RESULT_OK){
            initViewHolder()
        } else {
//            Toast.makeText(this, "Request accepted\nThanks!", Toast.LENGTH_LONG).show()
            AlertDialog.Builder(this@MessagesActivity)
                .setTitle(R.string.permissionRequired)
                .setMessage(R.string.defaultSmsApp)
                .setIcon(R.drawable.warning)
                .setPositiveButton(R.string.ok) { _, _ ->
                    // to make this app as default app for SMS messages
                    val setSmsAppIntent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
                    setSmsAppIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
                    startActivityForResult(setSmsAppIntent, REQUEST_DEFAULT_APP)
                }
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_SMS -> {
                if (grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setUpView()
                } else {
                    if (counterReadSms < 2) {
                        Toast.makeText(this@MessagesActivity, R.string.readSmsPermission, Toast.LENGTH_LONG).show()
                        counterReadSms++
                    } else {
                        alertDialog(R.string.permissionRequired, R.string.readSmsExplanation, R.drawable.warning, R.string.ok)
                    }
                }
            }
        }
    }

    private fun alertDialog(title: Int, message: Int, icon: Int, positiveButton: Int) {
        AlertDialog.Builder(this@MessagesActivity)
            .setTitle(title)
            .setMessage(message)
            .setIcon(icon)
            .setPositiveButton(positiveButton) { _, _ -> }
            .show()
    }

    override fun onTextReceived(address: String?, msg: String?) {
        smsViewModel.insert(SmsEntity(address!!, msg!!))
    }
}