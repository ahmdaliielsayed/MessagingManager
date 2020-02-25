package com.message.messagingmanager.model

class Contact {

    private lateinit var contactId: String
    private lateinit var contactName: String
    private lateinit var contactNumber: String
    private lateinit var groupId: String

    constructor()

    constructor(contactId: String, contactName: String, contactNumber: String, groupId: String) {
        this.contactId = contactId
        this.contactName = contactName
        this.contactNumber = contactNumber
        this.groupId = groupId
    }


    fun getContactId(): String{ return contactId }

    fun getContactName(): String{ return contactName }

    fun getContactNumber(): String{ return contactNumber }

    fun getGroupId(): String{ return groupId }
}