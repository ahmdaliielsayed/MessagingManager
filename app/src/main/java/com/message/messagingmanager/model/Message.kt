package com.message.messagingmanager.model

class Message {

    private lateinit var smsId: String
    private lateinit var smsReceiverName: String
    private lateinit var smsReceiverNumber: String
    private lateinit var smsMsg: String
    private lateinit var smsDate: String
    private lateinit var smsTime: String
    private lateinit var smsStatus: String
    private lateinit var smsType: String
    private lateinit var userID: String
    private var smsCalender: Long = 0

    constructor()

    constructor(smsId: String, smsReceiverName: String, smsReceiverNumber: String, smsMsg: String,
                smsDate: String, smsTime: String, smsStatus: String, smsType: String, userID: String,
                smsCalender: Long) {
        this.smsId = smsId
        this.smsReceiverName = smsReceiverName
        this.smsReceiverNumber = smsReceiverNumber
        this.smsMsg = smsMsg
        this.smsDate = smsDate
        this.smsTime = smsTime
        this.smsStatus = smsStatus
        this.smsType = smsType
        this.smsCalender = smsCalender
        this.userID = userID
    }

    fun getSmsId(): String{ return smsId }

    fun getSmsReceiverName(): String{ return smsReceiverName }

    fun getSmsReceiverNumber(): String{ return smsReceiverNumber }

    fun getSmsMsg(): String{ return smsMsg }

    fun getSmsDate(): String{ return smsDate }

    fun getSmsTime(): String{ return smsTime }

    fun getSmsStatus(): String{ return smsStatus }

    fun getSmsType(): String{ return smsType }

    fun getUserID(): String{ return userID }

    fun getSmsCalender(): Long{ return smsCalender }
}