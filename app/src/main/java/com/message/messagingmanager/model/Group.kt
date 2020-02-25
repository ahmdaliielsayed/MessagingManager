package com.message.messagingmanager.model

class Group {

    private lateinit var groupId: String
    private lateinit var groupName: String
    private lateinit var userID: String

    constructor()

    constructor(groupId: String, groupName: String, userID: String) {
        this.groupId = groupId
        this.groupName = groupName
        this.userID = userID
    }

    fun getGroupId(): String{ return groupId }

    fun getGroupName(): String{ return groupName }

    fun getUserID(): String{ return userID }
}