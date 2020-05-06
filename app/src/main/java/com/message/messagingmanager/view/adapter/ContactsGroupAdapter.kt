package com.message.messagingmanager.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.message.messagingmanager.HomeActivity
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Contact
import com.message.messagingmanager.model.Contacts
import com.message.messagingmanager.view.activity.ContactsGroupActivity
import kotlinx.android.synthetic.main.activity_contacts_group.*

class ContactsGroupAdapter(
    private val contactsList: ArrayList<Contacts>, private var contactsGroupActivity: ContactsGroupActivity,
    private val groupId: String) : RecyclerView.Adapter<ContactsGroupAdapter.DataViewHolder>() {

    private var groupContactList: ArrayList<Contacts> = ArrayList()
    private var deleteGroupContactList: ArrayList<Contact> = ArrayList()
    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid
    private var databaseReferenceContact: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Groups").child(groupId).child("Contacts")

    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_select_person, parent, false)
        prepareInterstitialAd()
        return DataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onViewRecycled(holder: DataViewHolder) {
        holder.getImgViewSelect()!!.isPressed = false
        super.onViewRecycled(holder)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        databaseReferenceContact.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (contactSnapshot in dataSnapshot.children) {
                    val contact = contactSnapshot.getValue(Contact::class.java)

                    if (contact!!.getGroupId() == groupId) {
                        deleteGroupContactList.add(contact)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        val contact = contactsList[position]

        holder.getCardView()!!.setOnClickListener {
            contactsList[holder.adapterPosition]
            if (groupContactList.contains(contact)) {
                holder.getImgViewSelect()!!.visibility = View.INVISIBLE
                groupContactList.remove(contact)
            } else {
                holder.getImgViewSelect()!!.visibility = View.VISIBLE
                groupContactList.add(contact)
            }
        }

        holder.getTxtViewPersonName()!!.text = contact.getName()
        holder.getTxtViewPhoneNumber()!!.text = contact.getPhone()

        contactsGroupActivity.btnDone.setOnClickListener {
            // 4. Check if the ad has loaded
            // 5. Display ad
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            }

            when {
                groupContactList.isEmpty() -> {
                    Toast.makeText(contactsGroupActivity, R.string.emptyGroup, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    if (deleteGroupContactList.size > 0){
                        for (i in 0 until deleteGroupContactList.size) {
                            for (j in 0 until groupContactList.size){
                                if (groupContactList[j].getName() == deleteGroupContactList[i].getContactName()){
                                    databaseReferenceContact.child(deleteGroupContactList[i].getContactId()).removeValue()
                                }
                            }
                        }
                    }

                    for (item in groupContactList.indices) {
                        val receiverNumber: String
                        val contactId = databaseReferenceContact.push().key.toString()
                        @Suppress("NAME_SHADOWING")

                        receiverNumber = if (groupContactList[item].getPhone().trim().length == 11) {
                            "+2" + groupContactList[item].getPhone()
                        } else if (groupContactList[item].getPhone().trim().length == 13 && groupContactList[item].getPhone().trim().contains(" ")) {
                            "+2" + groupContactList[item].getPhone()
                        } else {
                            groupContactList[item].getPhone().trim()
                        }

                        val contact = Contact(contactId, groupContactList[item].getName(), receiverNumber, groupId)
                        databaseReferenceContact.child(contactId).setValue(contact)
                    }

                    Toast.makeText(contactsGroupActivity, R.string.groupCreated, Toast.LENGTH_SHORT).show()
                    contactsGroupActivity.startActivity(Intent(contactsGroupActivity, HomeActivity::class.java))
                    contactsGroupActivity.finish()
                }
            }
        }
    }

    private fun prepareInterstitialAd() {
        // 1. Create InterstitialAd object

        // 1. Create InterstitialAd object
        mInterstitialAd = InterstitialAd(contactsGroupActivity)
        mInterstitialAd.adUnitId = contactsGroupActivity.getText(R.string.interstitialAdId).toString()
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
                Toast.makeText(contactsGroupActivity, contactsGroupActivity.getText(R.string.welcomeBack).toString(), Toast.LENGTH_SHORT).show()
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

        private var cardView: CardView? = null
        private var txtViewPersonName: TextView? = null
        private var imgViewSelect: ImageView? = null
        private var txtViewPhoneNumber: TextView? = null

        fun getCardView(): CardView? {
            if (cardView == null) {
                cardView = itemView.findViewById(R.id.cardView)
            }
            return cardView
        }

        fun getTxtViewPersonName(): TextView? {
            if (txtViewPersonName == null) {
                txtViewPersonName = itemView.findViewById(R.id.txtViewPersonName)
            }
            return txtViewPersonName
        }

        fun getImgViewSelect(): ImageView? {
            if (imgViewSelect == null) {
                imgViewSelect = itemView.findViewById(R.id.imgViewSelect)
            }
            return imgViewSelect
        }

        fun getTxtViewPhoneNumber(): TextView? {
            if (txtViewPhoneNumber == null) {
                txtViewPhoneNumber = itemView.findViewById(R.id.txtViewPhoneNumber)
            }
            return txtViewPhoneNumber
        }
    }
}