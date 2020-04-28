package com.message.messagingmanager.view.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.message.messagingmanager.R
import com.message.messagingmanager.viewmodel.SignInViewModel
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.app_bar.*


class SignInActivity : AppCompatActivity() {

    private lateinit var signInViewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        toolbar.setTitle(R.string.login)
        setSupportActionBar(toolbar)

        signInViewModel = ViewModelProviders.of(this, SignInViewModelFactory(this@SignInActivity))
            .get(SignInViewModel::class.java)

        progressBar.visibility = View.GONE

        txtViewForgetPassword.setOnClickListener {
            startActivity(Intent(this@SignInActivity, ForgetPasswordActivity::class.java))
        }

        btnSignIn.setOnClickListener {
            val email = editTxtEmailSignIn.text.toString().trim()
            val password = editTxtPasswordSignIn.text.toString().trim()

            if (email.isEmpty()) {
                editTxtEmailSignIn.error = getText(R.string.emailRequired)
                editTxtEmailSignIn.requestFocus()
                return@setOnClickListener
            } else if (password.isEmpty()) {
                editTxtPasswordSignIn.error = getText(R.string.passwordRequired)
                editTxtPasswordSignIn.requestFocus()
                return@setOnClickListener
            } else if (!isNetworkConnected()) {
                AlertDialog.Builder(this@SignInActivity)
                    .setTitle(R.string.error)
                    .setMessage(R.string.internetConnection)
                    .setIcon(R.drawable.cancel)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .show()
                return@setOnClickListener
            } else {
                progressBar.visibility = View.VISIBLE
                signInViewModel.loginViewModel(email, password)
            }
        }

        txtViewDonNotHaveAccount.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            finish()
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
        AlertDialog.Builder(this@SignInActivity)
            .setTitle(R.string.error)
            .setMessage(msg)
            .setIcon(R.drawable.cancel)
            .setPositiveButton(R.string.ok) { _, _ -> }
            .show()
    }

    fun setMsgAlert(msg: String) {
        progressBar.visibility = View.GONE
        AlertDialog.Builder(this@SignInActivity)
            .setTitle("Error")
            .setMessage(msg)
            .setIcon(R.drawable.cancel)
            .setPositiveButton("Ok") { _, _ -> }
            .show()
    }

    inner class SignInViewModelFactory(private val signInActivity: SignInActivity) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SignInViewModel(signInActivity) as T
        }
    }
}
