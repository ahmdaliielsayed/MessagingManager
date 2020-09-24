package com.message.messagingmanager.view.activity.messages

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class SmsViewModel(application: Application) : AndroidViewModel(application) {

    private var mRepository: SmsRepository? = null

    init {
        mRepository = SmsRepository(application)
    }

    fun insert(sms: SmsEntity?) {
        mRepository!!.insert(sms)
    }

    fun getAllSms(): MutableLiveData<List<SmsEntity>> {
        return mRepository!!.getAllWords()
    }
}