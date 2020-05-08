package com.message.messagingmanager.ui.groups

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Contact
import com.message.messagingmanager.model.Group
import com.message.messagingmanager.view.activity.ContactsDialogueActivity
import com.message.messagingmanager.view.activity.ContactsGroupActivity
import java.util.*
import kotlin.collections.ArrayList

class GroupsViewModel(internal var context: Activity, private val groupsList: ArrayList<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var searchGroupsList: ArrayList<Group> = ArrayList()

    private var dataModelList = ArrayList<Any>()

    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid
    private var databaseGroup = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Groups")

    private lateinit var mInterstitialAd: InterstitialAd

    // A row item view type.
    private val ROW_ITEM_VIEW_TYPE = 0

    // The banner ad view type.
    private val BANNER_AD_VIEW_TYPE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        prepareInterstitialAd()
        prepareSearchGroupList()

        val itemView: View
        return when(viewType) {
            ROW_ITEM_VIEW_TYPE -> {
                itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_group, parent, false)
                DataViewHolder(itemView)
            }
            else -> {
                itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_ad_view, parent, false)
                AdViewHolder(itemView)
            }
        }
    }

    private fun prepareSearchGroupList() {
        for (item in groupsList.indices) {
            if (groupsList[item] is Group) {
                searchGroupsList.add(groupsList[item] as Group)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(getItemViewType(position)) {
            ROW_ITEM_VIEW_TYPE -> {
                holder as DataViewHolder

//                if (position == 0) {
//                    holder.getConstraintLayoutHide()!!.visibility = View.GONE
//                } else {
//                    // put all code below in this scope حط الكود اللي تحت القوس ده هناا
//                }

                val group = dataModelList[position] as Group

                holder.getTxtViewGroupName()!!.text = group.getGroupName()

                holder.getConstraintLayout()!!.setOnClickListener {
                    val openNoteDialogue = Intent(context, ContactsDialogueActivity::class.java)
                    openNoteDialogue.putExtra("groupID", group.getGroupId())
                    openNoteDialogue.putExtra("groupName", group.getGroupName())
                    context.startActivity(openNoteDialogue)
                }

                holder.getImgViewAddToGroup()!!.setOnClickListener {
                    // 4. Check if the ad has loaded
                    // 5. Display ad
                    if (mInterstitialAd.isLoaded) {
                        mInterstitialAd.show()
                    }

                    val intent = Intent(context, ContactsGroupActivity::class.java)
                    intent.putExtra("groupId", group.getGroupId())
                    context.startActivity(intent)
                }

                holder.getImgViewDeleteGroup()!!.setOnClickListener {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage(R.string.deleteGroup)
                    builder.setPositiveButton(R.string.yes) { _, _ ->
                        // 4. Check if the ad has loaded
                        // 5. Display ad
                        if (mInterstitialAd.isLoaded) {
                            mInterstitialAd.show()
                        }

                        databaseGroup.child(group.getGroupId()).removeValue()

                        val databaseContacts = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Groups").child(group.getGroupId()).child("Contacts")

                        // delete related contacts
                        databaseContacts.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (noteSnapshot in dataSnapshot.children) {
                                    val contacts = noteSnapshot.getValue(Contact::class.java)

                                    if (contacts!!.getGroupId() == group.getGroupId()) {
                                        databaseContacts.child(contacts.getContactId()).removeValue()
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {

                            }
                        })

                        Toast.makeText(context, R.string.confirmGroupDeletion, Toast.LENGTH_SHORT).show()

                        dataModelList.remove(group)
                        setDataToAdapter(dataModelList)
                    }
                    builder.setNegativeButton(R.string.no) { dialogInterface, _ -> dialogInterface.cancel() }

                    val alertDialog = builder.create()
                    if (Build.VERSION.SDK_INT >= 26) {
                        alertDialog.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL)
                    } else {
                        alertDialog.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                    }
                    alertDialog.setCanceledOnTouchOutside(false)
                    alertDialog.show()
                }
            }
            else -> {
                holder as AdViewHolder
                if (dataModelList[position] is AdView) {
                    val adView = dataModelList[position] as AdView
                    val adCardView = holder.itemView as ViewGroup
                    // The AdViewHolder recycled by the RecyclerView may be a different
                    // instance than the one used previously for this position. Clear the
                    // AdViewHolder of any subviews in case it has a different
                    // AdView associated with it, and make sure the AdView for this position doesn't
                    // already have a parent of a different recycled AdViewHolder.
                    // The AdViewHolder recycled by the RecyclerView may be a different
                    // instance than the one used previously for this position. Clear the
                    // AdViewHolder of any subviews in case it has a different
                    // AdView associated with it, and make sure the AdView for this position doesn't
                    // already have a parent of a different recycled AdViewHolder.
                    if (adCardView.childCount > 0) {
                        adCardView.removeAllViews()
                    }
                    if (adView.parent != null) {
                        (adView.parent as ViewGroup).removeView(adView)
                    }

                    // Add the banner ad to the ad view.

                    // Add the banner ad to the ad view.
                    adCardView.addView(adView)
//                // 1. Place an AdView
//                holder.getAdView()!!.adListener = object : AdListener() {
//                    override fun onAdLoaded() {
//                        // Code to be executed when an ad finishes loading.
//
//                        // executed when an ad has finished loading.
//                        // If you want to delay adding the AdView to your activity or fragment until you're sure an ad will be loaded,
//                        // for example, you can do so here.
//
//                        // بتتنده كل مرة بيحصل update لـ الإعلاان و بيحصل update كل شوية
////                Toast.makeText(this@ScheduleMessageActivity, "ده لماا الإعلاان بيحمل", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onAdFailedToLoad(errorCode: Int) {
//                        // Code to be executed when an ad request fails.
////                Toast.makeText(this@ScheduleMessageActivity, "onAdFailedToLoad(int errorCode): $errorCode\nده لماا الإعلاان مبيحملش", Toast.LENGTH_SHORT).show()
////                when (errorCode) {
////                    AdRequest.ERROR_CODE_INTERNAL_ERROR -> Toast.makeText(this@ScheduleMessageActivity, "Something happened internally; for instance, an invalid response was received from the ad server.", Toast.LENGTH_SHORT).show()
////                    AdRequest.ERROR_CODE_INVALID_REQUEST -> Toast.makeText(this@ScheduleMessageActivity, "The ad request was invalid; for instance, the ad unit ID was incorrect.", Toast.LENGTH_SHORT).show()
////                    AdRequest.ERROR_CODE_NETWORK_ERROR -> Toast.makeText(this@ScheduleMessageActivity, "The ad request was unsuccessful due to network connectivity.", Toast.LENGTH_SHORT).show()
////                    AdRequest.ERROR_CODE_NO_FILL -> Toast.makeText(this@ScheduleMessageActivity, "The ad request was successful, but no ad was returned due to lack of ad inventory.", Toast.LENGTH_SHORT).show()
////                    AdRequest.ERROR_CODE_APP_ID_MISSING -> Toast.makeText(this@ScheduleMessageActivity, "APP_ID_MISSING", Toast.LENGTH_SHORT).show()
////                }
//                    }
//
//                    override fun onAdOpened() {
//                        // Code to be executed when an ad opens an overlay that covers the screen.
//                        // This method is invoked when the user taps on an ad.
////                Toast.makeText(this@ScheduleMessageActivity, "لماا بيفتح الإعلاان", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onAdClicked() {
//                        // Code to be executed when the user clicks on an ad.
//                        // مش بيوصلهاا !!!
////                Toast.makeText(BannerActivity.this, "onAdClicked()", Toast.LENGTH_SHORT).show();
//                    }
//
//                    override fun onAdLeftApplication() {
//                        // Code to be executed when the user has left the app.
//
//                        // This method is invoked after onAdOpened(),
//                        // when a user click opens another app (such as the Google Play), backgrounding the current app.
////                Toast.makeText(this@ScheduleMessageActivity, "لماا بيفتح الإعلاان أخرج من الأبلكيشن", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onAdClosed() {
//                        // Code to be executed when the user is about to return to the app after tapping on an ad.
//
//                        // When a user returns to the app after viewing an ad's destination URL, this method is invoked.
//                        // Your app can use it to resume suspended activities or perform any other work necessary to make itself ready for interaction.
////                Toast.makeText(this@ScheduleMessageActivity, "لماا أضغط ع الإعلاان و يفتح و أخرج من الإعلاان و أرجع لـ الـ application ده اللي هيحصل", Toast.LENGTH_SHORT).show()
//                        AlertDialog.Builder(context)
//                            .setTitle(R.string.welcomeBack)
//                            .setMessage(R.string.missYou)
//                            .setIcon(R.drawable.fire)
//                            .setPositiveButton(R.string.ok) { _, _ -> }
//                            .show()
//                    }
//                }
//                // 2. Build a request
//                val adRequest = AdRequest.Builder().build()
//                // 3.Load an ad
//                holder.getAdView()!!.loadAd(adRequest)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (dataModelList.size > 0) dataModelList.size else 0
    }

    override fun getItemViewType(position: Int): Int {
//        if (position % 4 == 0 && position != 0) {
//            // position != 0 ==> if you want to not showing the ad in the first row
//            return BANNER_AD_VIEW_TYPE
//        }

        if (position % 6 == 0) {
            return BANNER_AD_VIEW_TYPE
        }

        return ROW_ITEM_VIEW_TYPE
    }

    fun setDataToAdapter(dataModelList: ArrayList<Any>) {
        this.dataModelList = dataModelList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredSearchContactsList: ArrayList<Any> = ArrayList()

                if (constraint == null || constraint.isEmpty()) {
                    filteredSearchContactsList.addAll(searchGroupsList)
                } else {
                    val filterPattern: String = constraint.toString().toLowerCase(Locale.getDefault())
                        .trim()
                    for (item in searchGroupsList) {
                        if (item.getGroupName().toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                            filteredSearchContactsList.add(item)
                        }
                    }
                }

                val filterResult = FilterResults()
                filterResult.values = filteredSearchContactsList
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                groupsList.clear()
                groupsList.addAll(results!!.values as Collection<Group>)
                notifyDataSetChanged()
            }
        }
    }

    private fun prepareInterstitialAd() {
        // 1. Create InterstitialAd object

        // 1. Create InterstitialAd object
        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = context.getString(R.string.interstitialAdId)
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
                Toast.makeText(context, context.getString(R.string.welcomeBack), Toast.LENGTH_SHORT).show()
                // Load the next interstitial.
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        // 2. Request an ad
        // 2. Request an ad
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        // 3. Wait until the right moment
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var txtViewGroupName: TextView? = null
        private var imgViewAddToGroup: ImageView? = null
        private var imgViewDeleteGroup: ImageView? = null
        private var constraintLayout: ConstraintLayout? = null

        fun getTxtViewGroupName(): TextView? {
            if (txtViewGroupName == null) {
                txtViewGroupName = itemView.findViewById(R.id.txtViewGroupName)
            }
            return txtViewGroupName
        }
        fun getImgViewAddToGroup(): ImageView? {
            if (imgViewAddToGroup == null) {
                imgViewAddToGroup = itemView.findViewById(R.id.imgViewAddToGroup)
            }
            return imgViewAddToGroup
        }
        fun getImgViewDeleteGroup(): ImageView? {
            if (imgViewDeleteGroup == null) {
                imgViewDeleteGroup = itemView.findViewById(R.id.imgViewDeleteGroup)
            }
            return imgViewDeleteGroup
        }
        fun getConstraintLayout(): ConstraintLayout? {
            if (constraintLayout == null) {
                constraintLayout = itemView.findViewById(R.id.constraintLayout)
            }
            return constraintLayout
        }
    }

    inner class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var adView: AdView? = null

        fun getAdView(): AdView? {
            if (adView == null) {
                adView = itemView.findViewById(R.id.adView)
            }
            return adView
        }
    }
}