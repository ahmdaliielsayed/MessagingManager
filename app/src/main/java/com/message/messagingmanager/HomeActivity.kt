package com.message.messagingmanager

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.message.messagingmanager.ui.groups.GroupsFragment
import com.message.messagingmanager.ui.history.HistoryFragment
import com.message.messagingmanager.ui.upcoming.UpcomingFragment
import com.message.messagingmanager.view.activity.*
import kotlinx.android.synthetic.main.app_bar.*


@SuppressLint("Registered")
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        if (Telephony.Sms.getDefaultSmsPackage(this) != packageName) {
//            // App is not default.
//            // Show the "not currently set as the default SMS app" interface
//            //val viewGroup: View = findViewById(R.id.not_default_app)
//            //viewGroup.setVisibility(View.VISIBLE)
//
//            // Set up a button that allows the user to change the default SMS app
//            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
//            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
//            startActivity(intent)
//
////            val setSmsAppIntent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
////            setSmsAppIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
////            startActivityForResult(setSmsAppIntent, 5)
//        } else {
//            // App is the default.
//            // Hide the "not currently set as the default SMS app" interface
//            //val viewGroup: View = findViewById(R.id.not_default_app)
//            //viewGroup.setVisibility(View.GONE)
//        }

        UpcomingFragment.activiy = this
        HistoryFragment.activiy = this
        GroupsFragment.activiy = this

        setSupportActionBar(toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.elevation = 10F
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_upcoming, R.id.navigation_groups, R.id.navigation_history
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    @SuppressLint("BatteryLife")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_tips -> {
                startActivity(Intent(this@HomeActivity, TipsActivity::class.java))
            }
            R.id.item_emailUs -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.data = Uri.parse("email")
                val s = arrayOf("ahmdaliielsayed@gmail.com")
                intent.putExtra(Intent.EXTRA_EMAIL, s)
                intent.type = "message/rfc822"
                val chooser = Intent.createChooser(intent, "Launch Gmail")
                startActivity(chooser)
            }
            R.id.item_rateUS -> {
                val uri = Uri.parse("market://details?id=$packageName")
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                try {
                    startActivity(goToMarket)
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
                }
            }
            R.id.item_SIM -> {
                startActivity(Intent(this@HomeActivity, NetworksActivity::class.java))
            }
            R.id.item_privacyPolicy -> {
                startActivity(Intent(this@HomeActivity, PrivacyPolicyActivity::class.java))
            }
            R.id.item_termsAndConditions -> {
                startActivity(Intent(this@HomeActivity, TermsAndConditionsActivity::class.java))
            }
            R.id.item_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@HomeActivity, SignInActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
