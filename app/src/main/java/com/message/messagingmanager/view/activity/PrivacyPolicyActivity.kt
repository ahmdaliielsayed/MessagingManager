package com.message.messagingmanager.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.message.messagingmanager.R
import kotlinx.android.synthetic.main.app_bar.*

class PrivacyPolicyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        toolbar.setTitle(R.string.privacyPolicy)
        setSupportActionBar(toolbar)
    }
}
