package com.message.messagingmanager.model

class SIM {

    private lateinit var simId: String
    private lateinit var simName: String
    private lateinit var simPrefix: String
    private lateinit var userId: String

    constructor()

    constructor(simId: String, simName: String, simPrefix: String, userId: String) {
        this.simId = simId
        this.simName = simName
        this.simPrefix = simPrefix
        this.userId = userId
    }

    fun getSimId(): String{ return simId }

    fun getSimName(): String{ return simName }

    fun getSimPrefix(): String{ return simPrefix }

    fun getUserId(): String{ return userId }
}