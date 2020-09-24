package com.message.messagingmanager.view.activity.messages

class SmsEntity {

    private lateinit var id: String
    private lateinit var threadId: String
    private lateinit var address: String
    private lateinit var type: String
    private lateinit var date: String
    private lateinit var msg: String

    constructor()

    constructor(address: String, msg: String) {
        this.id = ""
        this.threadId = ""
        this.address = address
        this.type = ""
        this.date = ""
        this.msg = msg
    }

    constructor(
        id: String,
        threadId: String,
        address: String,
        type: String,
        date: String,
        msg: String
    ) {
        this.id = id
        this.threadId = threadId
        this.address = address
        this.type = type
        this.date = date
        this.msg = msg
    }

    fun getId(): String? {
        return id
    }

    fun getThreadId(): String? {
        return threadId
    }

    fun getAddress(): String? {
        return address
    }

    fun getType(): String? {
        return type
    }

    fun getDate(): String? {
        return date
    }

    fun getMsg(): String? {
        return msg
    }
}