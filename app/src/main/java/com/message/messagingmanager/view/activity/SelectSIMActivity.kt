package com.message.messagingmanager.view.activity

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
import com.message.messagingmanager.model.SIM
import com.message.messagingmanager.view.adapter.SelectSIMAdapter
import kotlinx.android.synthetic.main.activity_networks.recyclerView
import kotlinx.android.synthetic.main.activity_select_sim.*
import kotlinx.android.synthetic.main.app_bar.*
import java.util.ArrayList

class SelectSIMActivity : AppCompatActivity() {

    private var simsAdapter: SelectSIMAdapter? = null
    private var simsArrayList: ArrayList<Any> = ArrayList()


    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid
    private var databaseSIMs = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("SIMs")

    // A banner ad is placed in every 8th position in the RecyclerView.
    val ITEMS_PER_AD = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_sim)

        toolbar.setTitle(R.string.selectSIM)
        setSupportActionBar(toolbar)

        txtViewNoItem.setOnClickListener {
            startActivity(Intent(this@SelectSIMActivity, NetworksActivity::class.java))
        }
    }

    public override fun onStart() {
        super.onStart()

        databaseSIMs.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                simsArrayList.clear()

                for (noteSnapshot in dataSnapshot.children) {
                    val sim = noteSnapshot.getValue(SIM::class.java)

                    if (sim!!.getUserId() == FirebaseAuth.getInstance().currentUser!!.uid) {
                        simsArrayList.add(sim)
                    }

                    setAdapter()
                }

                if (simsArrayList.size <= 0) {
                    findViewById<LinearLayout>(R.id.linearLayoutFragmentUpcoming).visibility = View.VISIBLE
                    return
                } else {
                    findViewById<LinearLayout>(R.id.linearLayoutFragmentUpcoming).visibility = View.GONE
                    return
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun setAdapter() {

        val manager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = manager

        // Update the RecyclerView item's list with banner ads.
        addBannerAds()
        loadBannerAds()

        simsAdapter = SelectSIMAdapter(this)
        recyclerView!!.adapter = simsAdapter
        setDataSource()
    }
    private fun setDataSource() {
        simsAdapter!!.setDataToAdapter(simsArrayList)
    }

    /**
     * Adds banner ads to the items list.
     */
    private fun addBannerAds() {
        // Loop through the items array and place a new banner ad in every ith position in
        // the items List.
        var i = 0
        while (i <= simsArrayList.size) {
            val adView = AdView(this@SelectSIMActivity)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = getString(R.string.adViewId)
            simsArrayList.add(i, adView)
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
        if (index >= simsArrayList.size) {
            return
        }
        val item: Any = simsArrayList[index] as? AdView
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
                AlertDialog.Builder(this@SelectSIMActivity)
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
