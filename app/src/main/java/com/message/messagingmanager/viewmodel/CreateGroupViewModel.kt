package com.message.messagingmanager.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.message.messagingmanager.firebaserepository.FireBaseRepository
import com.message.messagingmanager.view.activity.ContactsGroupActivity
import com.message.messagingmanager.view.activity.CreateGroupActivity

class CreateGroupViewModel(private var createGroupActivity: CreateGroupActivity) : ViewModel() {

    private var fireBaseRepository: FireBaseRepository = FireBaseRepository(this)

    fun createGroupViewModel(name: String) {
        fireBaseRepository.createGroupRepository(name)
    }

    fun goToSelectContacts(groupId: String){
        val intent = Intent(createGroupActivity.applicationContext, ContactsGroupActivity::class.java)
        intent.putExtra("groupId", groupId)
        createGroupActivity.startActivity(intent)
        createGroupActivity.finish()
    }
}