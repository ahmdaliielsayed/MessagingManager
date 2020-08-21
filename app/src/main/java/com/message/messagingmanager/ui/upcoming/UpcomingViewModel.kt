package com.message.messagingmanager.ui.upcoming

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Message
import com.message.messagingmanager.view.activity.EditScheduleMessageActivity
import com.message.messagingmanager.view.receiver.AlertReceiver
import java.util.*
import kotlin.collections.ArrayList

class UpcomingViewModel(internal var context: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var upcomingMsgList:ArrayList<Any>? = ArrayList()

    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid
    private var databaseMsg = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Messages")

    private lateinit var mInterstitialAd: InterstitialAd

    // A row item view type.
    private val ROW_ITEM_VIEW_TYPE = 0

    // The banner ad view type.
    private val BANNER_AD_VIEW_TYPE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        prepareInterstitialAd()

        val itemView: View
        return when(viewType) {
            ROW_ITEM_VIEW_TYPE -> {
                itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_upcoming_person, parent, false)
                DataViewHolder(itemView)
            }
            else -> {
                itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_ad_view, parent, false)
                AdViewHolder(itemView)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(getItemViewType(position)) {
            ROW_ITEM_VIEW_TYPE -> {
//                if (position == 0) {
//                    holder.getConstraintLayoutHide()!!.visibility = View.GONE
//                } else {
//                    // put all code below in this scope حط الكود اللي تحت القوس ده هناا
//                }

                if (upcomingMsgList!![position] is Message) {
                    holder as DataViewHolder

                    val msg = upcomingMsgList!![position] as Message

                    holder.getTxtViewDateUpcomingPerson()!!.text = msg.getSmsDate()
                    holder.getTxtViewTimeUpcomingPerson()!!.text = msg.getSmsTime()
                    holder.getTxtViewStatusUpcomingPerson()!!.text = msg.getSmsStatus()
                    holder.getTxtViewUpcomingPersonName()!!.text = msg.getSmsReceiverName()
                    holder.getTxtViewUpcomingPhoneNumber()!!.text = msg.getSmsReceiverNumber()
                    holder.getTxtViewUpcomingMessage()!!.text = msg.getSmsMsg()

                    if (msg.getSmsType() == "SMS"){
                        holder.getImgViewMsgType()!!.setImageResource(R.drawable.sms)
                    } else {
                        holder.getImgViewMsgType()!!.setImageResource(R.drawable.whatsapp)
                    }

                    holder.getImageViewExpandCard()!!.setOnClickListener { view ->
                        if (holder.getConstraintLayoutDetails()!!.visibility == View.VISIBLE) {
                            holder.getConstraintLayoutDetails()!!.visibility = View.GONE
                        } else {
                            holder.getConstraintLayoutDetails()!!.visibility = View.VISIBLE

                            val animation = AnimationUtils.loadAnimation(view.context, R.anim.open_card)
                            animation.duration = 500
                            holder.getConstraintLayoutDetails()!!.animation = animation
                            holder.getConstraintLayoutDetails()!!.animate()
                            animation.start()
                        }
                    }

                    holder.getImageButtonPopUp()!!.setOnClickListener {
                        val popupMenu = PopupMenu(context, holder.getImageButtonPopUp())
                        popupMenu.menuInflater.inflate(R.menu.menu_upcoming_msg_person, popupMenu.menu)
                        popupMenu.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.item_edit -> {
                                    val intent = Intent(context, EditScheduleMessageActivity::class.java)
                                    intent.putExtra("SmsId", msg.getSmsId())
                                    intent.putExtra("SmsReceiverName", msg.getSmsReceiverName())
                                    intent.putExtra("SmsReceiverNumber", msg.getSmsReceiverNumber())
                                    intent.putExtra("SmsMsg", msg.getSmsMsg())
                                    intent.putExtra("SmsDate", msg.getSmsDate())
                                    intent.putExtra("SmsTime", msg.getSmsTime())
                                    intent.putExtra("SmsStatus", msg.getSmsStatus())
                                    intent.putExtra("SmsType", msg.getSmsType())
                                    intent.putExtra("UserID", msg.getUserID())
                                    intent.putExtra("calendar", msg.getSmsCalender())
                                    context.startActivity(intent)
                                }
                                R.id.item_delete -> {
                                    val builder = AlertDialog.Builder(context)
                                    builder.setMessage(R.string.deleteMsg)
                                    builder.setPositiveButton(R.string.yes) { _, _ ->
                                        // 4. Check if the ad has loaded
                                        // 5. Display ad
                                        if (mInterstitialAd.isLoaded) {
                                            mInterstitialAd.show()
                                        }
                                        databaseMsg.child(msg.getSmsId()).removeValue()

                                        val alarmManager =
                                            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                                        val iDelete = Intent(context, AlertReceiver::class.java)
                                        iDelete.putExtra("SmsId", msg.getSmsId())
                                        iDelete.putExtra("SmsReceiverName", msg.getSmsReceiverName())
                                        iDelete.putExtra("SmsReceiverNumber", msg.getSmsReceiverNumber())
                                        iDelete.putExtra("SmsMsg", msg.getSmsMsg())
                                        iDelete.putExtra("SmsDate", msg.getSmsDate())
                                        iDelete.putExtra("SmsTime", msg.getSmsTime())
                                        iDelete.putExtra("SmsStatus", msg.getSmsStatus())
                                        iDelete.putExtra("SmsType", msg.getSmsType())
                                        iDelete.putExtra("UserID", msg.getUserID())
                                        iDelete.putExtra("calendar", msg.getSmsCalender())

                                        val pendingIntent = PendingIntent.getBroadcast(context,
                                            msg.getSmsId().hashCode(), iDelete,
                                            PendingIntent.FLAG_UPDATE_CURRENT)
                                        alarmManager.cancel(pendingIntent)

                                        Toast.makeText(context, R.string.confirmMsgDeletion,
                                            Toast.LENGTH_SHORT).show()

                                        upcomingMsgList!!.remove(msg)
                                        setDataToAdapter(upcomingMsgList!!)
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

                                R.id.item_send -> {
                                    // 4. Check if the ad has loaded
                                    // 5. Display ad
                                    if (mInterstitialAd.isLoaded) {
                                        mInterstitialAd.show()
                                    }
                                    val alarmManager =
                                        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                                    val iDone = Intent(context, AlertReceiver::class.java)
                                    iDone.putExtra("SmsId", msg.getSmsId())
                                    iDone.putExtra("SmsReceiverName", msg.getSmsReceiverName())
                                    iDone.putExtra("SmsReceiverNumber", msg.getSmsReceiverNumber())
                                    iDone.putExtra("SmsMsg", msg.getSmsMsg())
                                    iDone.putExtra("SmsDate", msg.getSmsDate())
                                    iDone.putExtra("SmsTime", msg.getSmsTime())
                                    iDone.putExtra("SmsStatus", msg.getSmsStatus())
                                    iDone.putExtra("SmsType", msg.getSmsType())
                                    iDone.putExtra("UserID", msg.getUserID())
                                    iDone.putExtra("calendar", msg.getSmsCalender())

                                    val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context,
                                        msg.getSmsId().hashCode(), iDone, PendingIntent.FLAG_UPDATE_CURRENT)

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, Calendar.getInstance().time.time, pendingIntent)
                                        } else {
                                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().time.time, pendingIntent)
                                        }
                                    } else {
                                        alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().time.time, pendingIntent)
                                    }

                                    Toast.makeText(context, R.string.sendMsgNow, Toast.LENGTH_SHORT).show()

                                    upcomingMsgList!!.remove(msg)
                                    setDataToAdapter(upcomingMsgList!!)
                                }
                            }
                            true
                        }
                        popupMenu.show()
                    }
                }
            }
            else -> {
                holder as AdViewHolder
                val adView = upcomingMsgList!![position] as AdView
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

    override fun getItemCount(): Int {
        return if (upcomingMsgList == null) 0 else upcomingMsgList!!.size
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

    fun setDataToAdapter(upcomingMsgList: ArrayList<Any>) {
        this.upcomingMsgList = upcomingMsgList
        notifyDataSetChanged()
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

        private var txtViewDateUpcomingPerson: TextView? = null
        private var txtViewTimeUpcomingPerson: TextView? = null
        private var txtViewStatusUpcomingPerson: TextView? = null
        private var txtViewUpcomingPersonName: TextView? = null
        private var txtViewUpcomingPhoneNumber: TextView? = null
        private var txtViewUpcomingMessage: TextView? = null
        private var imgViewMsgType: ImageView? = null
        private var constraintLayoutExpandCard: ConstraintLayout? = null
        private var imageButtonPopUp: ImageButton? = null
        private var constraintLayoutDetails: ConstraintLayout? = null

//        private var constraintLayoutHide: ConstraintLayout? = null

        fun getTxtViewDateUpcomingPerson(): TextView? {
            if (txtViewDateUpcomingPerson == null) {
                txtViewDateUpcomingPerson = itemView.findViewById(R.id.txtViewDateUpcomingPerson)
            }
            return txtViewDateUpcomingPerson
        }
        fun getTxtViewTimeUpcomingPerson(): TextView? {
            if (txtViewTimeUpcomingPerson == null) {
                txtViewTimeUpcomingPerson = itemView.findViewById(R.id.txtViewTimeUpcomingPerson)
            }
            return txtViewTimeUpcomingPerson
        }
        fun getTxtViewStatusUpcomingPerson(): TextView? {
            if (txtViewStatusUpcomingPerson == null) {
                txtViewStatusUpcomingPerson = itemView.findViewById(R.id.txtViewStatusUpcomingPerson)
            }
            return txtViewStatusUpcomingPerson
        }
        fun getTxtViewUpcomingPersonName(): TextView? {
            if (txtViewUpcomingPersonName == null) {
                txtViewUpcomingPersonName = itemView.findViewById(R.id.txtViewUpcomingPersonName)
            }
            return txtViewUpcomingPersonName
        }
        fun getTxtViewUpcomingPhoneNumber(): TextView? {
            if (txtViewUpcomingPhoneNumber == null) {
                txtViewUpcomingPhoneNumber = itemView.findViewById(R.id.txtViewUpcomingPhoneNumber)
            }
            return txtViewUpcomingPhoneNumber
        }
        fun getTxtViewUpcomingMessage(): TextView? {
            if (txtViewUpcomingMessage == null) {
                txtViewUpcomingMessage = itemView.findViewById(R.id.txtViewUpcomingMessage)
            }
            return txtViewUpcomingMessage
        }
        fun getImgViewMsgType(): ImageView? {
            if (imgViewMsgType == null) {
                imgViewMsgType = itemView.findViewById(R.id.imgViewMsgType)
            }
            return imgViewMsgType
        }
        fun getImageViewExpandCard(): ConstraintLayout? {
            if (constraintLayoutExpandCard == null) {
                constraintLayoutExpandCard = itemView.findViewById(R.id.constraintLayout)
            }
            return constraintLayoutExpandCard
        }
        fun getImageButtonPopUp(): ImageButton? {
            if (imageButtonPopUp == null) {
                imageButtonPopUp = itemView.findViewById(R.id.imageButtonPopUp)
            }
            return imageButtonPopUp
        }
        fun getConstraintLayoutDetails(): ConstraintLayout? {
            if (constraintLayoutDetails == null) {
                constraintLayoutDetails = itemView.findViewById(R.id.constraintLayoutDetails)
            }
            return constraintLayoutDetails
        }
//        fun getConstraintLayoutHide(): ConstraintLayout? {
//            if (constraintLayoutHide == null) {
//                constraintLayoutHide = itemView.findViewById(R.id.hide)
//            }
//            return constraintLayoutHide
//        }
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