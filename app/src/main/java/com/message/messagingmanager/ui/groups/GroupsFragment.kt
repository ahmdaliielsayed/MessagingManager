package com.message.messagingmanager.ui.groups

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Group
import com.message.messagingmanager.view.activity.CreateGroupActivity

class GroupsFragment() : Fragment() {

    private lateinit var groupsViewModel: GroupsViewModel
    private var recyclerView: RecyclerView? = null

    private lateinit var databaseReferenceGroup: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userId: String

    internal lateinit var groupsList: ArrayList<Any>

    internal lateinit var view: View

    private var textVal: String = ""

    // A banner ad is placed in every 8th position in the RecyclerView.
    val ITEMS_PER_AD = 6

    companion object{
        @SuppressLint("StaticFieldLeak")
        var activiy: Activity = Activity()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)

        view = inflater.inflate(R.layout.fragment_groups, container, false)
        recyclerView = view.findViewById(R.id.recyclerGroup)

        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        databaseReferenceGroup = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Groups")
        databaseReferenceGroup.keepSynced(true)
        groupsList = ArrayList()

        val floatingActionButtonGroups = view.findViewById<FloatingActionButton>(R.id.floatingActionButtonGroups)
        floatingActionButtonGroups.setOnClickListener {
            startActivity(Intent(activity, CreateGroupActivity::class.java))
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        databaseReferenceGroup.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                groupsList.clear()

                for (msgSnapshot in dataSnapshot.children) {
                    val group = msgSnapshot.getValue(Group::class.java)

                    if (group!!.getUserID() == FirebaseAuth.getInstance().currentUser!!.uid) {
                        groupsList.add(group)
                    }
                }

                if (groupsList.size <= 0) {
                    view.refreshDrawableState()
                    view.findViewById<LinearLayout>(R.id.linearLayoutFragmentGroup).visibility = View.VISIBLE
                    view.refreshDrawableState()
                } else {
                    view.refreshDrawableState()
                    view.findViewById<LinearLayout>(R.id.linearLayoutFragmentGroup).visibility = View.GONE
                    setAdapter()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun setAdapter() {

        val manager = LinearLayoutManager(activiy)
        recyclerView!!.layoutManager = manager

        // Update the RecyclerView item's list with banner ads.
        addBannerAds()
        loadBannerAds()

        groupsViewModel = GroupsViewModel(activiy, groupsList)
        recyclerView!!.adapter = groupsViewModel
        setDataSource()
    }

    private fun setDataSource() {
        groupsViewModel.setDataToAdapter(groupsList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)

        val searchItem: MenuItem = menu.findItem(R.id.item_search)
        val searchView: SearchView = searchItem.actionView as SearchView

        // to remove search icon from keyboard because we just on locale recyclerview not online recyclerview
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        // to set search text
        searchView.queryHint = getText(R.string.searchGroup)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                groupsViewModel.filter.filter(newText)
                if (newText != null) {
                    textVal = newText
                }
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                groupsViewModel.filter.filter(textVal)
                // change color
                val id = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
                val editText = searchView.findViewById<View>(id) as EditText
                editText.setHintTextColor(Color.WHITE)
//                editText.setText("")
//                adapter.filter.filter("")
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                groupsViewModel.filter.filter("")
                return true
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
        while (i <= groupsList.size) {
            val adView = AdView(context)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = getString(R.string.adViewId)
            groupsList.add(i, adView)
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
        if (index >= groupsList.size) {
            return
        }
        val item: Any = groupsList[index] as? AdView
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
                AlertDialog.Builder(context!!)
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