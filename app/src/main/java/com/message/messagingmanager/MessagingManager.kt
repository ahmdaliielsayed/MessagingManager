package com.message.messagingmanager

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MessagingManager : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}