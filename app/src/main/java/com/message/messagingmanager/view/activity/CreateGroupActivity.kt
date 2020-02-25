package com.message.messagingmanager.view.activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.message.messagingmanager.R
import com.message.messagingmanager.viewmodel.CreateGroupViewModel
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.activity_create_group.progressBar
import kotlinx.android.synthetic.main.app_bar.*

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var createGroupViewModel: CreateGroupViewModel

    private val PERMISSION_REQUEST_READ_CONTACTS = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        progressBar.visibility = View.GONE

        createGroupViewModel = ViewModelProviders.of(this, CreateGroupViewModelFactory(this@CreateGroupActivity))
            .get(CreateGroupViewModel::class.java)

        toolbar.title = "Create Group"
        setSupportActionBar(toolbar)

        btnCreate.setOnClickListener {
            if (editTxtGroupName.text.toString().isEmpty()){
                editTxtGroupName.error = "Group Name Required!"
                editTxtGroupName.requestFocus()
                return@setOnClickListener
            } else {
                val permissionCheck: Int = ContextCompat.checkSelfPermission(this@CreateGroupActivity, Manifest.permission.READ_CONTACTS)
                if (permissionCheck == PackageManager.PERMISSION_GRANTED){
                    progressBar.visibility = View.VISIBLE
                    createGroupViewModel.createGroupViewModel(editTxtGroupName.text.toString())
                } else {
                    ActivityCompat.requestPermissions(this@CreateGroupActivity, arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_READ_CONTACTS)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            PERMISSION_REQUEST_READ_CONTACTS -> {
                if (grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    progressBar.visibility = View.VISIBLE
                    createGroupViewModel.createGroupViewModel(editTxtGroupName.text.toString())
                } else {
                    Toast.makeText(this@CreateGroupActivity, "Give us the permission to read your contacts!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    inner class CreateGroupViewModelFactory(private val createGroupActivity: CreateGroupActivity) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CreateGroupViewModel(createGroupActivity) as T
        }
    }
}
