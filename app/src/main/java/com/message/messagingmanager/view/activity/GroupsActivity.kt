package com.message.messagingmanager.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Group
import com.message.messagingmanager.view.adapter.GroupsAdapter
import kotlinx.android.synthetic.main.activity_contacts.progressBar
import kotlinx.android.synthetic.main.activity_contacts.recyclerView
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.app_bar.*

class GroupsActivity : AppCompatActivity() {

    private var arrGroups: ArrayList<Any> = ArrayList()

    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid
    private var databaseReferenceGroups: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Groups")

    // A banner ad is placed in every 8th position in the RecyclerView.
    val ITEMS_PER_AD = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        toolbar.setTitle(R.string.selectGroup)
        setSupportActionBar(toolbar)

        databaseReferenceGroups.keepSynced(true)

        txtViewNoItemAddGroup.setOnClickListener {
            startActivity(Intent(this@GroupsActivity, CreateGroupActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        progressBar.visibility = View.VISIBLE
        databaseReferenceGroups.addValueEventListener(object : ValueEventListener {
            @SuppressLint("WrongConstant")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                arrGroups.clear()

                for (msgSnapshot in dataSnapshot.children) {
                    val group = msgSnapshot.getValue(Group::class.java)

                    if (group!!.getUserID() == FirebaseAuth.getInstance().currentUser!!.uid) {
                        arrGroups.add(group)
                    }
                }

                if (arrGroups.size <= 0) {
                    progressBar.visibility = View.GONE
                    findViewById<LinearLayout>(R.id.linearLayoutFragment).visibility = View.VISIBLE
                } else {
                    findViewById<LinearLayout>(R.id.linearLayoutFragment).visibility = View.GONE

                    val adapter = GroupsAdapter(arrGroups, this@GroupsActivity)
                    recyclerView.layoutManager = LinearLayoutManager(this@GroupsActivity, LinearLayout.VERTICAL, false)

                    // Update the RecyclerView item's list with banner ads.
                    addBannerAds()
                    loadBannerAds()

                    recyclerView.adapter = adapter

                    progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    /**
     * Adds banner ads to the items list.
     */
    private fun addBannerAds() {
        // Loop through the items array and place a new banner ad in every ith position in
        // the items List.
        var i = 0
        while (i <= arrGroups.size) {
            val adView = AdView(this@GroupsActivity)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = getString(R.string.adViewId)
            arrGroups.add(i, adView)
            i += ITEMS_PER_AD
        }
    }

    /**
     * Sets up and loads the banner ads.
     */
    private fun loadBannerAds() {
        // Load the first banner ad in the items list (subsequent ads will be loaded automatically
        // in sequence).
        loadBannerAd(0)
    }

    /**
     * Loads the banner ads in the items list.
     */
    private fun loadBannerAd(index: Int) {
        if (index >= arrGroups.size) {
            return
        }
        val item: Any = arrGroups[index] as? AdView
            ?: throw ClassCastException(
                "Expected item at index " + index + " to be a banner ad"
                        + " ad."
            )
        val adView = item as AdView

        // Set an AdListener on the AdView to wait for the previous banner ad
        // to finish loading before loading the next ad in the items list.
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                // Code to be executed when an ad finishes loading.

                // executed when an ad has finished loading.
                // If you want to delay adding the AdView to your activity or fragment until you're sure an ad will be loaded,
                // for example, you can do so here.

                // بتتنده كل مرة بيحصل update لـ الإعلاان و بيحصل update كل شوية

                // The previous banner ad loaded successfully, call this method again to load the next ad in the items list.
                loadBannerAd(index + ITEMS_PER_AD)
            }

            override fun onAdFailedToLoad(errorCode: Int) {
//                // Code to be executed when an ad request fails.
//                Toast.makeText(this@ScheduleMessageActivity, "onAdFailedToLoad(int errorCode): $errorCode\nده لماا الإعلاان مبيحملش", Toast.LENGTH_SHORT).show()
//                when (errorCode) {
//                    AdRequest.ERROR_CODE_INTERNAL_ERROR -> Toast.makeText(this@ScheduleMessageActivity, "Something happened internally; for instance, an invalid response was received from the ad server.", Toast.LENGTH_SHORT).show()
//                    AdRequest.ERROR_CODE_INVALID_REQUEST -> Toast.makeText(this@ScheduleMessageActivity, "The ad request was invalid; for instance, the ad unit ID was incorrect.", Toast.LENGTH_SHORT).show()
//                    AdRequest.ERROR_CODE_NETWORK_ERROR -> Toast.makeText(this@ScheduleMessageActivity, "The ad request was unsuccessful due to network connectivity.", Toast.LENGTH_SHORT).show()
//                    AdRequest.ERROR_CODE_NO_FILL -> Toast.makeText(this@ScheduleMessageActivity, "The ad request was successful, but no ad was returned due to lack of ad inventory.", Toast.LENGTH_SHORT).show()
//                    AdRequest.ERROR_CODE_APP_ID_MISSING -> Toast.makeText(this@ScheduleMessageActivity, "APP_ID_MISSING", Toast.LENGTH_SHORT).show()
//                }

                // The previous banner ad failed to load. Call this method again to load the next ad in the items list.
                Log.e("MainActivity", "The previous banner ad failed to load. Attempting to load the next banner ad in the items list.")
                loadBannerAd(index + ITEMS_PER_AD)
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return to the app after tapping on an ad.

                // When a user returns to the app after viewing an ad's destination URL, this method is invoked.
                // Your app can use it to resume suspended activities or perform any other work necessary to make itself ready for interaction.
//                Toast.makeText(this@ScheduleMessageActivity, "لماا أضغط ع الإعلاان و يفتح و أخرج من الإعلاان و أرجع لـ الـ application ده اللي هيحصل", Toast.LENGTH_SHORT).show()
                AlertDialog.Builder(this@GroupsActivity)
                    .setTitle(R.string.welcomeBack)
                    .setMessage(R.string.missYou)
                    .setIcon(R.drawable.fire)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .show()
            }
        }

        // Load the banner ad.
        adView.loadAd(AdRequest.Builder().build())
    }
}
