package com.message.messagingmanager.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.message.messagingmanager.HomeActivity
import com.message.messagingmanager.R

class SplashActivity : AppCompatActivity() {

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        // Call this method only once and as early as possible, ideally at app launch. (I think at splash screen)
        MobileAds.initialize(this, getString(R.string.adMobAppId))

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        handler = Handler()
        handler.postDelayed({
            if (firebaseUser != null && firebaseUser.isEmailVerified) {
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
                finish()
            }
        }, 2000)
    }
}
