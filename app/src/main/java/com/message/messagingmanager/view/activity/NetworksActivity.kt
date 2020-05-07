package com.message.messagingmanager.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.message.messagingmanager.R
import com.message.messagingmanager.model.SIM
import com.message.messagingmanager.view.adapter.SIMAdapter
import com.message.messagingmanager.viewmodel.NetworksViewModel
import kotlinx.android.synthetic.main.activity_networks.*
import kotlinx.android.synthetic.main.app_bar.*
import java.util.ArrayList

class NetworksActivity : AppCompatActivity() {

    private var simsAdapter: SIMAdapter? = null
    private var simsArrayList: ArrayList<Any>? = null

    private lateinit var mAuth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var databaseSIMs: DatabaseReference

    private lateinit var networksViewModel: NetworksViewModel

    private lateinit var adView: AdView
    private lateinit var mInterstitialAd: InterstitialAd

    // A banner ad is placed in every 8th position in the RecyclerView.
    val ITEMS_PER_AD = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_networks)

        makeAd()
        prepareInterstitialAd()

        toolbar.setTitle(R.string.configureNetworks)
        setSupportActionBar(toolbar)

        networksViewModel = ViewModelProviders.of(this, NetworksViewModelFactory(this@NetworksActivity))
            .get(NetworksViewModel::class.java)

        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        databaseSIMs = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("SIMs")
        databaseSIMs.keepSynced(true)

        btnAddPlus.setOnClickListener {
            when {
                editTxtAddNetwork.text.toString().trim() == "" -> {
                    editTxtAddNetwork.error = getText(R.string.simName)
                    editTxtAddNetwork.requestFocus()
                }
                editTxtAddPrefix.text.toString().trim() == "" -> {
                    editTxtAddPrefix.error = getText(R.string.prefixRequired)
                    editTxtAddPrefix.requestFocus()
                }
                !editTxtAddPrefix.text.toString().startsWith("+") -> {
                    editTxtAddPrefix.error = getText(R.string.prefixRestriction)
                    editTxtAddPrefix.requestFocus()
                }
                editTxtAddPrefix.text.toString().trim().length < 2 -> {
                    editTxtAddPrefix.error = getText(R.string.prefixError)
                    editTxtAddPrefix.requestFocus()
                }
                else -> {
                    // 4. Check if the ad has loaded
                    // 5. Display ad
                    if (mInterstitialAd.isLoaded) {
                        mInterstitialAd.show()
                    }

                    for (item in simsArrayList!!.indices) {
                        if ((simsArrayList!![item] as SIM).getSimPrefix() == editTxtAddPrefix.text.toString().trim()) {
                            AlertDialog.Builder(this@NetworksActivity)
                                .setTitle(R.string.error)
                                .setMessage(R.string.prefixExist)
                                .setIcon(R.drawable.cancel)
                                .setPositiveButton(R.string.ok) { _, _ -> editTxtAddPrefix.setText("") }
                                .show()

                            return@setOnClickListener
                        }
                    }

                    networksViewModel.addSIMViewModel(editTxtAddNetwork.text.toString().trim(), editTxtAddPrefix.text.toString().trim())
                }
            }
        }

        simsArrayList = ArrayList()
    }

    fun setMsgToast(msg: Int) {
        editTxtAddNetwork.setText("")
        editTxtAddPrefix.setText("")

        Toast.makeText(this@NetworksActivity, msg, Toast.LENGTH_SHORT).show()
    }

    public override fun onStart() {
        super.onStart()

        databaseSIMs.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                simsArrayList!!.clear()

                for (noteSnapshot in dataSnapshot.children) {
                    val sim = noteSnapshot.getValue(SIM::class.java)

                    if (sim!!.getUserId() == FirebaseAuth.getInstance().currentUser!!.uid) {
                        simsArrayList!!.add(sim)
                    }

                    setAdapter()
                }

                if (simsArrayList!!.size <= 0) {
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

        simsAdapter = SIMAdapter(this)
        recyclerView!!.adapter = simsAdapter
        setDataSource()
    }
    private fun setDataSource() {
        simsAdapter!!.setDataToAdapter(simsArrayList!!)
    }

    private fun makeAd() {
        // 1. Place an AdView
        adView = findViewById(R.id.adView)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.

                // executed when an ad has finished loading.
                // If you want to delay adding the AdView to your activity or fragment until you're sure an ad will be loaded,
                // for example, you can do so here.

                // بتتنده كل مرة بيحصل update لـ الإعلاان و بيحصل update كل شوية
//                Toast.makeText(this@ScheduleMessageActivity, "ده لماا الإعلاان بيحمل", Toast.LENGTH_SHORT).show()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
//                Toast.makeText(this@ScheduleMessageActivity, "onAdFailedToLoad(int errorCode): $errorCode\nده لماا الإعلاان مبيحملش", Toast.LENGTH_SHORT).show()
//                when (errorCode) {
//                    AdRequest.ERROR_CODE_INTERNAL_ERROR -> Toast.makeText(this@ScheduleMessageActivity, "Something happened internally; for instance, an invalid response was received from the ad server.", Toast.LENGTH_SHORT).show()
//                    AdRequest.ERROR_CODE_INVALID_REQUEST -> Toast.makeText(this@ScheduleMessageActivity, "The ad request was invalid; for instance, the ad unit ID was incorrect.", Toast.LENGTH_SHORT).show()
//                    AdRequest.ERROR_CODE_NETWORK_ERROR -> Toast.makeText(this@ScheduleMessageActivity, "The ad request was unsuccessful due to network connectivity.", Toast.LENGTH_SHORT).show()
//                    AdRequest.ERROR_CODE_NO_FILL -> Toast.makeText(this@ScheduleMessageActivity, "The ad request was successful, but no ad was returned due to lack of ad inventory.", Toast.LENGTH_SHORT).show()
//                    AdRequest.ERROR_CODE_APP_ID_MISSING -> Toast.makeText(this@ScheduleMessageActivity, "APP_ID_MISSING", Toast.LENGTH_SHORT).show()
//                }
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that covers the screen.
                // This method is invoked when the user taps on an ad.
//                Toast.makeText(this@ScheduleMessageActivity, "لماا بيفتح الإعلاان", Toast.LENGTH_SHORT).show()
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                // مش بيوصلهاا !!!
//                Toast.makeText(BannerActivity.this, "onAdClicked()", Toast.LENGTH_SHORT).show();
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.

                // This method is invoked after onAdOpened(),
                // when a user click opens another app (such as the Google Play), backgrounding the current app.
//                Toast.makeText(this@ScheduleMessageActivity, "لماا بيفتح الإعلاان أخرج من الأبلكيشن", Toast.LENGTH_SHORT).show()
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return to the app after tapping on an ad.

                // When a user returns to the app after viewing an ad's destination URL, this method is invoked.
                // Your app can use it to resume suspended activities or perform any other work necessary to make itself ready for interaction.
//                Toast.makeText(this@ScheduleMessageActivity, "لماا أضغط ع الإعلاان و يفتح و أخرج من الإعلاان و أرجع لـ الـ application ده اللي هيحصل", Toast.LENGTH_SHORT).show()
                AlertDialog.Builder(this@NetworksActivity)
                    .setTitle(R.string.welcomeBack)
                    .setMessage(R.string.missYou)
                    .setIcon(R.drawable.fire)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .show()
            }
        }
        // 2. Build a request
        val adRequest = AdRequest.Builder().build()
        // 3.Load an ad
        adView.loadAd(adRequest)
    }

    private fun prepareInterstitialAd() {
        // 1. Create InterstitialAd object

        // 1. Create InterstitialAd object
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getText(R.string.interstitialAdId).toString()
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.

                // executed when an ad has finished loading.
                // If you want to delay adding the AdView to your activity or fragment until you're sure an ad will be loaded,
                // for example, you can do so here.

//                Toast.makeText(this@ScheduleMessageActivity, "ده لماا الإعلاان بيحمل", Toast.LENGTH_SHORT).show()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
//                Toast.makeText(this@ScheduleMessageActivity, "onAdFailedToLoad(int errorCode): $errorCode\nده لماا الإعلاان مبيحملش", Toast.LENGTH_SHORT).show()
//                when (errorCode) {
//                    AdRequest.ERROR_CODE_INTERNAL_ERROR -> Toast.makeText(this@ScheduleMessageActivity, "Something happened internally; for instance, an invalid response was received from the ad server.", Toast.LENGTH_SHORT).show()
//                    AdRequest.ERROR_CODE_INVALID_REQUEST -> Toast.makeText(this@ScheduleMessageActivity, "The ad request was invalid; for instance, the ad unit ID was incorrect.", Toast.LENGTH_SHORT).show()
//                    AdRequest.ERROR_CODE_NETWORK_ERROR -> Toast.makeText(this@ScheduleMessageActivity, "The ad request was unsuccessful due to network connectivity.", Toast.LENGTH_SHORT).show()
//                    AdRequest.ERROR_CODE_NO_FILL -> Toast.makeText(this@ScheduleMessageActivity, "The ad request was successful, but no ad was returned due to lack of ad inventory.", Toast.LENGTH_SHORT).show()
//                    AdRequest.ERROR_CODE_APP_ID_MISSING -> Toast.makeText(this@ScheduleMessageActivity, "APP_ID_MISSING", Toast.LENGTH_SHORT).show()
//                }
            }

            override fun onAdOpened() {
                // Code to be executed when the ad is displayed.

                // This method is invoked when the user taps on an ad.
//                Toast.makeText(this@ScheduleMessageActivity, "لماا بيفتح الإعلاان", Toast.LENGTH_SHORT).show()
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                // مش بيوصلهاا !!!
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.

                // This method is invoked after onAdOpened(),
                // when a user click opens another app (such as the Google Play), backgrounding the current app.
//                Toast.makeText(this@ScheduleMessageActivity, "لماا بيفتح الإعلاان أخرج من الأبلكيشن", Toast.LENGTH_SHORT).show()
            }

            override fun onAdClosed() {
                // Code to be executed when the interstitial ad is closed.

                // When a user returns to the app after viewing an ad's destination URL, this method is invoked.
                // Your app can use it to resume suspended activities or perform any other work necessary to make itself ready for interaction.
                Toast.makeText(this@NetworksActivity, getText(R.string.welcomeBack).toString(), Toast.LENGTH_SHORT).show()
                // Load the next interstitial.
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        // 2. Request an ad
        // 2. Request an ad
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        // 3. Wait until the right moment
    }

    /**
     * Adds banner ads to the items list.
     */
    private fun addBannerAds() {
        // Loop through the items array and place a new banner ad in every ith position in
        // the items List.
        var i = 0
        while (i <= simsArrayList!!.size) {
            val adView = AdView(this@NetworksActivity)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = getString(R.string.adViewId)
            simsArrayList!!.add(i, adView)
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
        if (index >= simsArrayList!!.size) {
            return
        }
        val item: Any = simsArrayList!![index] as? AdView
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
                AlertDialog.Builder(this@NetworksActivity)
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

    inner class NetworksViewModelFactory(private val networksActivity: NetworksActivity) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NetworksViewModel(networksActivity) as T
        }
    }
}
