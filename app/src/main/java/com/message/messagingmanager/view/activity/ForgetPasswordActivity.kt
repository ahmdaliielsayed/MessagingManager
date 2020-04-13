package com.message.messagingmanager.view.activity

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.message.messagingmanager.R
import com.message.messagingmanager.viewmodel.ForgetPasswordViewModel
import kotlinx.android.synthetic.main.activity_forget_password.*
import kotlinx.android.synthetic.main.app_bar.*

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var forgetPasswordViewModel: ForgetPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        toolbar.setTitle(R.string.passwordReset)
        setSupportActionBar(toolbar)

        forgetPasswordViewModel = ViewModelProviders.of(this, ForgetPasswordViewModelFactory(this@ForgetPasswordActivity))
            .get(ForgetPasswordViewModel::class.java)

        progressBar.visibility = View.GONE

        btnSend.setOnClickListener {
            val email = editTxtEmailForgetPassword.text.toString().trim()

            if (email.isEmpty()) {
                editTxtEmailForgetPassword.error = getText(R.string.emailRequired)
                editTxtEmailForgetPassword.requestFocus()
                return@setOnClickListener
            } else if (!isNetworkConnected()) {
                AlertDialog.Builder(this@ForgetPasswordActivity)
                    .setTitle(R.string.error)
                    .setMessage(R.string.internetConnection)
                    .setIcon(R.drawable.cancel)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .show()
                return@setOnClickListener
            } else {
                progressBar.visibility = View.VISIBLE
                forgetPasswordViewModel.changePasswordViewModel(email)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
    }

    fun setMsgAlert(msg: Int) {
        progressBar.visibility = View.GONE
        AlertDialog.Builder(this@ForgetPasswordActivity)
            .setTitle(R.string.emailSent)
            .setMessage(msg)
            .setIcon(R.drawable.ic_check_circle_green_24dp)
            .setPositiveButton(R.string.ok) { _, _ ->
                finish()
            }
            .show()
    }

    fun setMsgAlert(msg: String) {
        progressBar.visibility = View.GONE
        AlertDialog.Builder(this@ForgetPasswordActivity)
            .setTitle("Error")
            .setMessage(msg)
            .setIcon(R.drawable.cancel)
            .setPositiveButton("Ok") { _, _ -> }
            .show()
    }

    inner class ForgetPasswordViewModelFactory(private val forgetPasswordActivity: ForgetPasswordActivity) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ForgetPasswordViewModel(forgetPasswordActivity) as T
        }
    }
}
