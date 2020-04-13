package com.message.messagingmanager.view.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.message.messagingmanager.R
import com.message.messagingmanager.viewmodel.SignUpViewModel
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.progressBar
import kotlinx.android.synthetic.main.app_bar.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        toolbar.setTitle(R.string.register)
        setSupportActionBar(toolbar)

        signUpViewModel = ViewModelProviders.of(this, SignUpViewModelFactory(this@SignUpActivity))
            .get(SignUpViewModel::class.java)

        progressBar.visibility = View.GONE

        btnSignUp.setOnClickListener {
            val email = editTxtEmailSignUp.text.toString().trim()
            val password = editTxtPasswordSignUp.text.toString().trim()
            val confirmPassword = editTxtRePasswordSignUp.text.toString().trim()

            if (email.isEmpty()) {
                editTxtEmailSignUp.error = getText(R.string.emailRequired)
                editTxtEmailSignUp.requestFocus()
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTxtEmailSignUp.error = getText(R.string.validEmail)
                editTxtEmailSignUp.requestFocus()
                return@setOnClickListener
            } else if (password.isEmpty()) {
                editTxtPasswordSignUp.error = getText(R.string.passwordRequired)
                editTxtPasswordSignUp.requestFocus()
                return@setOnClickListener
            } else if (password.length < 6) {
                editTxtPasswordSignUp.error = getText(R.string.passwordLength)
                editTxtPasswordSignUp.requestFocus()
                return@setOnClickListener
            } else if (confirmPassword.isEmpty()) {
                editTxtRePasswordSignUp.error = getText(R.string.confirmPassword)
                editTxtRePasswordSignUp.requestFocus()
                return@setOnClickListener
            } else if (password != confirmPassword) {
                AlertDialog.Builder(this@SignUpActivity)
                    .setTitle(R.string.error)
                    .setMessage(R.string.passwordIncompatible)
                    .setIcon(R.drawable.cancel)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .show()
                return@setOnClickListener
            } else if (!isNetworkConnected()) {
                AlertDialog.Builder(this@SignUpActivity)
                    .setTitle(R.string.error)
                    .setMessage(R.string.internetConnection)
                    .setIcon(R.drawable.cancel)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .show()
                return@setOnClickListener
            } else {
                progressBar.visibility = View.VISIBLE
                signUpViewModel.signUpViewModel(email, password)
            }
        }

        txtViewAlreadyRegistered.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
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
        AlertDialog.Builder(this@SignUpActivity)
            .setTitle(R.string.verifyAccount)
            .setMessage(msg)
            .setIcon(R.drawable.ic_check_circle_green_24dp)
            .setPositiveButton(R.string.ok) { _, _ ->
                val intent = Intent(this@SignUpActivity, SignInActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
            .show()
    }

    fun setMsgAlert(msg: String) {
        progressBar.visibility = View.GONE
        AlertDialog.Builder(this@SignUpActivity)
            .setTitle("Error")
            .setMessage(msg)
            .setIcon(R.drawable.cancel)
            .setPositiveButton("Ok") { _, _ -> }
            .show()
    }

    inner class SignUpViewModelFactory(private val signUpActivity: SignUpActivity) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SignUpViewModel(signUpActivity) as T
        }
    }
}
