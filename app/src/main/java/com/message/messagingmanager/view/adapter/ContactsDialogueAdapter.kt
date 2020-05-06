package com.message.messagingmanager.view.adapter

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Contact
import java.util.ArrayList

class ContactsDialogueAdapter(internal var context: Context, internal var groupID: String) :
    RecyclerView.Adapter<ContactsDialogueAdapter.DataViewHolder>() {

    private var dataModelList = ArrayList<Contact>()

    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid
    private var databaseReferenceContacts = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Groups").child(groupID).child("Contacts")

    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_contact_dialogue, parent, false)
        prepareInterstitialAd()
        return DataViewHolder(itemView)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {

        val contact = dataModelList[position]

        holder.getTxtViewPersonNameDialogue()!!.text = contact.getContactName()
        holder.getTxtViewPhoneNumberDialogue()!!.text = contact.getContactNumber()

        holder.getIvDelete()!!.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.deletePerson)
            builder.setPositiveButton(R.string.yes) { _, _ ->
                // 4. Check if the ad has loaded
                // 5. Display ad
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }

                databaseReferenceContacts.child(contact.getContactId()).removeValue()
                Toast.makeText(context, R.string.personDeleted, Toast.LENGTH_SHORT).show()
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

    override fun getItemCount(): Int {
        return if (dataModelList.size > 0) dataModelList.size else 0
    }

    fun setDataToAdapter(dataModelList: ArrayList<Contact>) {
        this.dataModelList = dataModelList
        notifyDataSetChanged()
    }

    private fun prepareInterstitialAd() {
        // 1. Create InterstitialAd object

        // 1. Create InterstitialAd object
        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = context.getText(R.string.interstitialAdId).toString()
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
                Toast.makeText(context, context.getText(R.string.welcomeBack).toString(), Toast.LENGTH_SHORT).show()
                // Load the next interstitial.
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        // 2. Request an ad
        // 2. Request an ad
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        // 3. Wait until the right moment
    }

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var txtViewPersonNameDialogue: TextView? = null
        private var txtViewPhoneNumberDialogue: TextView? = null
        private var ivDelete: ImageView? = null

        fun getTxtViewPersonNameDialogue(): TextView? {
            if (txtViewPersonNameDialogue == null) {
                txtViewPersonNameDialogue = itemView.findViewById(R.id.txtViewPersonNameDialogue)
            }
            return txtViewPersonNameDialogue
        }

        fun getTxtViewPhoneNumberDialogue(): TextView? {
            if (txtViewPhoneNumberDialogue == null) {
                txtViewPhoneNumberDialogue = itemView.findViewById(R.id.txtViewPhoneNumberDialogue)
            }
            return txtViewPhoneNumberDialogue
        }

        fun getIvDelete(): ImageView? {
            if (ivDelete == null) {
                ivDelete = itemView.findViewById(R.id.ivDelete)
            }
            return ivDelete
        }
    }
}