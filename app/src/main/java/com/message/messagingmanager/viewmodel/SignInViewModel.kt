package com.message.messagingmanager.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.message.messagingmanager.HomeActivity
import com.message.messagingmanager.firebaserepository.FireBaseRepository
import com.message.messagingmanager.view.activity.SignInActivity

class SignInViewModel(private var signInActivity: SignInActivity) : ViewModel() {

    private var fireBaseRepository: FireBaseRepository = FireBaseRepository(this)

    fun loginViewModel(email: String, password: String) {
        fireBaseRepository.loginRepository(email, password)
    }

    fun openHomeActivity(){
        val intent = Intent(signInActivity, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        signInActivity.startActivity(intent)
    }

    fun setMsgAlert(msg: String) {
        signInActivity.setMsgAlert(msg)
    }

    fun setMsgAlert(msg: Int) {
        signInActivity.setMsgAlert(msg)
    }
}