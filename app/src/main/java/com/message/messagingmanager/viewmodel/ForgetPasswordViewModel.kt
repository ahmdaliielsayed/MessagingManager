package com.message.messagingmanager.viewmodel

import androidx.lifecycle.ViewModel
import com.message.messagingmanager.firebaserepository.FireBaseRepository
import com.message.messagingmanager.view.activity.ForgetPasswordActivity

class ForgetPasswordViewModel(private var forgetPasswordActivity: ForgetPasswordActivity) : ViewModel() {

    private var fireBaseRepository: FireBaseRepository = FireBaseRepository(this)

    fun changePasswordViewModel(email: String) {
        fireBaseRepository.changePasswordRepository(email)
    }

    fun setMsgAlertSuccess(msg: String) {
        forgetPasswordActivity.setMsgAlertSuccess(msg)
    }

    fun setMsgAlertError(msg: String) {
        forgetPasswordActivity.setMsgAlertError(msg)
    }
}