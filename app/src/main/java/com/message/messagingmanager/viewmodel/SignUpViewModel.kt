package com.message.messagingmanager.viewmodel

import androidx.lifecycle.ViewModel
import com.message.messagingmanager.firebaserepository.FireBaseRepository
import com.message.messagingmanager.view.activity.SignUpActivity

class SignUpViewModel(private var signUpActivity: SignUpActivity) : ViewModel() {

    private var fireBaseRepository: FireBaseRepository = FireBaseRepository(this)

    fun signUpViewModel(email: String, password: String) {
        fireBaseRepository.signUpRepository(email, password)
    }

    fun setMsgAlert(msg: Int) {
        signUpActivity.setMsgAlert(msg)
    }

    fun setMsgAlert(msg: String) {
        signUpActivity.setMsgAlert(msg)
    }
}