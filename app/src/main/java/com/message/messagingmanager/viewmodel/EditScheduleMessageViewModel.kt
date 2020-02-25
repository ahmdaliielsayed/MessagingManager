package com.message.messagingmanager.viewmodel

import androidx.lifecycle.ViewModel
import com.message.messagingmanager.firebaserepository.FireBaseRepository
import com.message.messagingmanager.view.activity.EditScheduleMessageActivity

class EditScheduleMessageViewModel(private var editScheduleMessageActivity: EditScheduleMessageActivity) :
    ViewModel() {

    private var fireBaseRepository: FireBaseRepository = FireBaseRepository(this)

    fun editScheduleMessageViewModel(smsID: String, personName: String, receiverNumber: String, message: String,
                                 date: String, time: String, status: String, type: String,
                                 calendar: Long) {

        fireBaseRepository.editScheduleMessageRepository(smsID, personName, receiverNumber, message, date, time,
            status, type, calendar)
    }
    fun setSMSAlarm(smsId: String, personName: String, receiverNumber: String, SMSMessage: String,
                    date: String, time: String, status: String, type: String, currentUser: String,
                    calendar: Long) {

        editScheduleMessageActivity.setSMSAlarm(smsId, personName, receiverNumber, SMSMessage, date,
            time, status, type, currentUser, calendar)
    }

    fun editScheduleWhatsAppMessageViewModel(smsId: String, personName: String, receiverNumber: String, message: String,
                                         date: String, time: String, status: String, type: String,
                                         calendar: Long) {

        fireBaseRepository.editScheduleWhatsAppMessageRepository(smsId, personName, receiverNumber, message, date, time,
            status, type, calendar)
    }
    fun setWhatsAppMessageAlarm(smsId: String, personName: String, receiverNumber: String, SMSMessage: String,
                                date: String, time: String, status: String, type: String, currentUser: String,
                                calendar: Long) {

        editScheduleMessageActivity.setWhatsAppMessageAlarm(smsId, personName, receiverNumber, SMSMessage, date,
            time, status, type, currentUser, calendar)
    }
}