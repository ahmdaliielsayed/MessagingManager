package com.message.messagingmanager.viewmodel

import androidx.lifecycle.ViewModel
import com.message.messagingmanager.firebaserepository.FireBaseRepository
import com.message.messagingmanager.view.activity.NetworksActivity

class NetworksViewModel(private var networksActivity: NetworksActivity) : ViewModel() {

    private var fireBaseRepository: FireBaseRepository = FireBaseRepository(this)

    fun addSIMViewModel(SIMName: String, SIMPrefix: String) {
        fireBaseRepository.addSIMRepository(SIMName, SIMPrefix)
    }

    fun setMsgToast(msg: String) {
        networksActivity.setMsgToast(msg)
    }
}