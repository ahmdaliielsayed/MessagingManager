package com.message.messagingmanager.model

class Contacts {

    private lateinit var name: String
    private lateinit var phone: String

    constructor()

    constructor(name: String, phone: String) {
        this.name = name
        this.phone = phone
    }

    fun getName(): String{
        return name
    }

    fun getPhone(): String{
        return phone
    }
}