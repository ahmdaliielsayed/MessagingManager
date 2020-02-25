package com.message.messagingmanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.message.messagingmanager.ui.groups.GroupsFragment
import com.message.messagingmanager.ui.history.HistoryFragment
import com.message.messagingmanager.ui.upcoming.UpcomingFragment
import com.message.messagingmanager.view.activity.NetworksActivity
import com.message.messagingmanager.view.activity.SignInActivity
import kotlinx.android.synthetic.main.app_bar.*
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.PowerManager
import com.message.messagingmanager.view.activity.TipsActivity

@SuppressLint("Registered")
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
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
                val s = arrayOf("ahmedali666680@yahoo.com", "fekragdida500@gmail.com")
                intent.putExtra(Intent.EXTRA_EMAIL, s)
                intent.type = "message/rfc822"
                val chooser = Intent.createChooser(intent, "Launch Gmail")
                startActivity(chooser)
            }
            R.id.item_callUs -> {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:+201063208399")
                startActivity(intent)
            }
            R.id.item_SIM -> {
                startActivity(Intent(this@HomeActivity, NetworksActivity::class.java))
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
