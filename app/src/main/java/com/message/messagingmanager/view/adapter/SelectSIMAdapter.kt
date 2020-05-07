package com.message.messagingmanager.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdView
import com.message.messagingmanager.R
import com.message.messagingmanager.model.SIM
import com.message.messagingmanager.view.activity.ScheduleNetworkMessagesActivity
import java.util.ArrayList

class SelectSIMAdapter(internal var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataModelList = ArrayList<Any>()

    // A row item view type.
    private val ROW_ITEM_VIEW_TYPE = 0

    // The banner ad view type.
    private val BANNER_AD_VIEW_TYPE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View
        return when(viewType) {
            ROW_ITEM_VIEW_TYPE -> {
                itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_select_sim, parent, false)
                DataViewHolder(itemView)
            }
            else -> {
                itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_ad_view, parent, false)
                AdViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(getItemViewType(position)) {
            ROW_ITEM_VIEW_TYPE -> {
                holder as DataViewHolder

//                if (position == 0) {
//                    holder.getConstraintLayoutHide()!!.visibility = View.GONE
//                } else {
//                    // put all code below in this scope حط الكود اللي تحت القوس ده هناا
//                }

                val sim = dataModelList[position] as SIM

                holder.getTxtViewSIMName()!!.text = sim.getSimName()
                holder.getTxtViewPrefix()!!.text = sim.getSimPrefix()

                holder.getConstraintLayout()!!.setOnClickListener {
                    val intent = Intent(context, ScheduleNetworkMessagesActivity::class.java)
                    intent.putExtra("SIMName", holder.getTxtViewSIMName()!!.text)
                    intent.putExtra("SIMPrefix", holder.getTxtViewPrefix()!!.text)
                    context.startActivity(intent)
                }
            }
            else -> {
                holder as AdViewHolder
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

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var txtViewSIMName: TextView? = null
        private var txtViewPrefix: TextView? = null
        private var constraintLayout: ConstraintLayout? = null

        fun getTxtViewSIMName(): TextView? {
            if (txtViewSIMName == null) {
                txtViewSIMName = itemView.findViewById(R.id.txtViewSIMName)
            }
            return txtViewSIMName
        }
        fun getTxtViewPrefix(): TextView? {
            if (txtViewPrefix == null) {
                txtViewPrefix = itemView.findViewById(R.id.txtViewPrefix)
            }
            return txtViewPrefix
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