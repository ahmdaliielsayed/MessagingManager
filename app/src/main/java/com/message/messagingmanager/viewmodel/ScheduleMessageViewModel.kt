package com.message.messagingmanager.viewmodel

import androidx.lifecycle.ViewModel
import com.message.messagingmanager.firebaserepository.FireBaseRepository
import com.message.messagingmanager.view.activity.ScheduleMessageActivity

class ScheduleMessageViewModel(private var scheduleMessageActivity: ScheduleMessageActivity) :
    ViewModel() {

    private var fireBaseRepository: FireBaseRepository = FireBaseRepository(this)

    fun scheduleMessageViewModel(personName: String, receiverNumber: String, message: String,
                                 date: String, time: String, status: String, type: String,
                                 calendar: Long) {

        fireBaseRepository.scheduleMessageRepository(personName, receiverNumber, message, date, time,
            status, type, calendar)
    }
    fun setSMSAlarm(smsId: String, personName: String, receiverNumber: String, SMSMessage: String,
                   date: String, time: String, status: String, type: String, currentUser: String,
                   calendar: Long) {

        scheduleMessageActivity.setSMSAlarm(smsId, personName, receiverNumber, SMSMessage, date,
            time, status, type, currentUser, calendar)
    }

    fun scheduleWhatsAppMessageViewModel(personName: String, receiverNumber: String, message: String,
                                 date: String, time: String, status: String, type: String,
                                 calendar: Long) {

        fireBaseRepository.scheduleWhatsAppMessageRepository(personName, receiverNumber, message, date, time,
            status, type, calendar)
    }
    fun setWhatsAppMessageAlarm(smsId: String, personName: String, receiverNumber: String, SMSMessage: String,
                    date: String, time: String, status: String, type: String, currentUser: String,
                    calendar: Long) {

        scheduleMessageActivity.setWhatsAppMessageAlarm(smsId, personName, receiverNumber, SMSMessage, date,
            time, status, type, currentUser, calendar)
    }
}