package com.message.messagingmanager.view.activity

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.message.messagingmanager.R
import com.message.messagingmanager.WhatsappAccessibilityService
import com.message.messagingmanager.view.receiver.AlertReceiver
import com.message.messagingmanager.viewmodel.EditScheduleMessageViewModel
import kotlinx.android.synthetic.main.activity_edit_schedule_message.*
import kotlinx.android.synthetic.main.app_bar.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EditScheduleMessageActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_SEND_SMS = 1
    private val PERMISSION_REQUEST_READ_CONTACTS = 2

    private var calender: DatePickerDialog.OnDateSetListener? = null
    private var timePickerDialog: TimePickerDialog? = null

    private var date: Date? = null
    private var myDateCheck: Date? = null
    private var mHour = 0
    private var mMin = 0
    private var hours1 = 0
    private var min1 = 0
    private var year1 = 0
    private var month1 = 0
    private var dayOfMonth1 = 0

    private var calendarAlarm: Calendar? = null

    private var smsId: String = ""
    private var smsStatus: String = ""
    private var smsType: String = ""
    private var userID: String = ""
    private var calendar: Long = 0

    private lateinit var editScheduleMessageViewModel: EditScheduleMessageViewModel

    private lateinit var adView: AdView
    private lateinit var mInterstitialAd: InterstitialAd

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_schedule_message)

        makeAd()
        prepareInterstitialAd()

        editScheduleMessageViewModel = ViewModelProviders.of(this, EditScheduleMessageViewModelFactory(this@EditScheduleMessageActivity))
            .get(EditScheduleMessageViewModel::class.java)

        toolbar.setTitle(R.string.editScheduledMessages)
        setSupportActionBar(toolbar)

        /*** date ***/
        editTxtDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(this@EditScheduleMessageActivity, calender, year, month, day)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialog.show()
        }
        calender = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            @Suppress("NAME_SHADOWING")
            var month = month
            month += 1

            year1 = year
            month1 = month
            dayOfMonth1 = dayOfMonth

            val startDate = "$dayOfMonth/$month/$year"
            val time = SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            try {
                date = format.parse(time)
                myDateCheck = format.parse(startDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            when {
                myDateCheck == null -> try {
                    myDateCheck = format.parse(time)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                myDateCheck!!.before(date) -> Toast.makeText(this@EditScheduleMessageActivity, R.string.enterValidDate, Toast.LENGTH_LONG).show()
                else -> {
                    editTxtDate.setText(startDate)
                    editTxtTime.setText("")
                }
            }
        }

        /*** time ***/
        editTxtTime.setOnClickListener {
            val c = Calendar.getInstance()
            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMin = c.get(Calendar.MINUTE)

            timePickerDialog = TimePickerDialog(this@EditScheduleMessageActivity,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
                    val myCalInstance = Calendar.getInstance()
                    val myRealCalender = Calendar.getInstance()

                    if (myDateCheck == null) {
                        val timeStamp = SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)
                        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        try {
                            myDateCheck = format.parse(timeStamp)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }

                    }

                    myRealCalender.time = myDateCheck
                    myRealCalender.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    myRealCalender.set(Calendar.MINUTE, minutes)

                    if (myRealCalender.time.before(myCalInstance.time)) {
                        Toast.makeText(this@EditScheduleMessageActivity, R.string.enterValidTime, Toast.LENGTH_LONG).show()
                    } else {
                        hours1 = hourOfDay
                        min1 = minutes
                        calendarAlarm = Calendar.getInstance()
                        calendarAlarm!!.set(Calendar.YEAR, year1)
                        calendarAlarm!!.set(Calendar.MONTH, month1 - 1)
                        calendarAlarm!!.set(Calendar.DAY_OF_MONTH, dayOfMonth1)
                        calendarAlarm!!.set(Calendar.HOUR_OF_DAY, hours1)
                        calendarAlarm!!.set(Calendar.MINUTE, min1)
                        calendarAlarm!!.set(Calendar.SECOND, 0)
                        if (hourOfDay < 10 && minutes >= 10) {
                            editTxtTime.setText("0$hourOfDay:$minutes")
                        } else if (hourOfDay < 10 && minutes < 10) {
                            editTxtTime.setText("0$hourOfDay:0$minutes")
                        } else if (hourOfDay >= 10 && minutes < 10) {
                            editTxtTime.setText("$hourOfDay:0$minutes")
                        } else if (hourOfDay >= 10 && minutes >= 10) {
                            editTxtTime.setText("$hourOfDay:$minutes")
                        }
                    }
                }, mHour, mMin, false
            )
            timePickerDialog!!.show()
        }

        btnSMS.setOnClickListener {

//            AlertDialog.Builder(this@EditScheduleMessageActivity)
//                .setTitle(R.string.downloadNewerVersion)
//                .setMessage(R.string.downloadNewerVersionMessage)
//                .setIcon(R.drawable.ic_check_circle_green_24dp)
//                .setPositiveButton(R.string.ok) { _, _ ->
//                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mediafire.com/file/3v6b3rwxavpqs5i/Message+Scheduler.apk/file")))
//                }
//                .setNegativeButton(R.string.cancel){ _, _ ->
//                    // 4. Check if the ad has loaded
//                    // 5. Display ad
//                    if (mInterstitialAd.isLoaded) {
//                        mInterstitialAd.show()
//                    }
//                    Toast.makeText(this@EditScheduleMessageActivity, R.string.cannotSendSMS, Toast.LENGTH_LONG).show()
//                }
//                .show()

            val permissionCheck: Int = ContextCompat.checkSelfPermission(this@EditScheduleMessageActivity, Manifest.permission.SEND_SMS)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED){
                // 4. Check if the ad has loaded
                // 5. Display ad
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
                editScheduleSMSMessage()
            } else {
                ActivityCompat.requestPermissions(this@EditScheduleMessageActivity, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_REQUEST_SEND_SMS)
            }
        }

        btnWhatsApp.setOnClickListener {
            if (isAccessibilityOn(this@EditScheduleMessageActivity, WhatsappAccessibilityService::class.java)) {
                // 4. Check if the ad has loaded
                // 5. Display ad
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
                editScheduleWhatsAppMessage()
            } else {
                AlertDialog.Builder(this@EditScheduleMessageActivity)
                    .setTitle(R.string.enableAccessibility)
                    .setMessage(R.string.accessibilitySteps)
                    .setIcon(R.drawable.ic_check_circle_green_24dp)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                    .show()
            }
        }

        imgViewContacts.setOnClickListener {
            // make EditContactsActivity to work without error
            val permissionCheck: Int = ContextCompat.checkSelfPermission(this@EditScheduleMessageActivity, Manifest.permission.READ_CONTACTS)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED){
                readContacts()
            } else {
                ActivityCompat.requestPermissions(this@EditScheduleMessageActivity, arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_READ_CONTACTS)
            }
        }

        smsId = intent.getStringExtra("SmsId")
        txtViewPersonName.text = intent.getStringExtra("SmsReceiverName")
        editTxtReceiverNumber.setText(intent.getStringExtra("SmsReceiverNumber"))
        editTxtMessage.setText(intent.getStringExtra("SmsMsg"))
        editTxtDate.setText(intent.getStringExtra("SmsDate"))
        editTxtTime.setText(intent.getStringExtra("SmsTime"))
        smsStatus = intent.getStringExtra("SmsStatus")
        smsType = intent.getStringExtra("SmsType")
        if (smsType == "SMS"){
            btnWhatsApp.visibility = View.GONE
            btnSMS.visibility = View.VISIBLE
        } else {
            btnSMS.visibility = View.GONE
            btnWhatsApp.visibility = View.VISIBLE
        }
        userID = intent.getStringExtra("UserID")
        calendar = intent.extras!!.getLong("calendar")
    }

    private fun editScheduleSMSMessage(){

        val receiverNumber: String
        if (validateInputs()){

            receiverNumber = if (editTxtReceiverNumber.text.toString().trim().length == 11) {
                "+2" + editTxtReceiverNumber.text.toString()
            } else if (editTxtReceiverNumber.text.toString().trim().length == 13 && editTxtReceiverNumber.text.toString().trim().contains(" ")) {
                "+2" + editTxtReceiverNumber.text.toString()
            } else {
                editTxtReceiverNumber.text.toString().trim()
            }

            if (calendarAlarm == null){
                editScheduleMessageViewModel.editScheduleMessageViewModel(smsId, txtViewPersonName.text.toString(),
                    receiverNumber, editTxtMessage.text.toString(), editTxtDate.text.toString(),
                    editTxtTime.text.toString(), resources.getString(R.string.status_upcoming), resources.getString(R.string.type_sms), calendar)
            } else {
                editScheduleMessageViewModel.editScheduleMessageViewModel(smsId, txtViewPersonName.text.toString(),
                    receiverNumber, editTxtMessage.text.toString(), editTxtDate.text.toString(),
                    editTxtTime.text.toString(), resources.getString(R.string.status_upcoming), resources.getString(R.string.type_sms), calendarAlarm!!.timeInMillis)
            }
        }
    }

    private fun editScheduleWhatsAppMessage(){

        val receiverNumber: String
        if (validateInputs()){

            receiverNumber = if (editTxtReceiverNumber.text.toString().trim().length == 11) {
                "+2" + editTxtReceiverNumber.text.toString()
            } else if (editTxtReceiverNumber.text.toString().trim().length == 13 && editTxtReceiverNumber.text.toString().trim().contains(" ")) {
                "+2" + editTxtReceiverNumber.text.toString()
            } else {
                editTxtReceiverNumber.text.toString().trim()
            }

            if (calendarAlarm == null){
                editScheduleMessageViewModel.editScheduleWhatsAppMessageViewModel(smsId, txtViewPersonName.text.toString(),
                    receiverNumber, editTxtMessage.text.toString(), editTxtDate.text.toString(),
                    editTxtTime.text.toString(), resources.getString(R.string.status_upcoming), resources.getString(R.string.type_whats_app), calendar)
            } else {
                editScheduleMessageViewModel.editScheduleWhatsAppMessageViewModel(smsId, txtViewPersonName.text.toString(),
                    receiverNumber, editTxtMessage.text.toString(), editTxtDate.text.toString(),
                    editTxtTime.text.toString(), resources.getString(R.string.status_upcoming), resources.getString(R.string.type_whats_app), calendarAlarm!!.timeInMillis)
            }
        }
    }

    private fun readContacts() {
        val intent = Intent(this@EditScheduleMessageActivity, ContactsEditActivity::class.java)
        intent.putExtra("SmsId", smsId)
        intent.putExtra("SmsReceiverName", txtViewPersonName.text)
        intent.putExtra("SmsReceiverNumber", editTxtReceiverNumber.text.toString().trim())
        intent.putExtra("SmsMsg", editTxtMessage.text.toString().trim())
        intent.putExtra("SmsDate", editTxtDate.text.toString().trim())
        intent.putExtra("SmsTime", editTxtTime.text.toString().trim())
        intent.putExtra("SmsStatus", smsStatus)
        intent.putExtra("SmsType", smsType)
        intent.putExtra("UserID", userID)
        intent.putExtra("calendar", calendar)
        startActivity(intent)
        finish()
    }

    private fun validateInputs() : Boolean{
        val phoneNum: String = editTxtReceiverNumber.text.toString().trim()
        val smsMessage: String = editTxtMessage.text.toString().trim()

        var valid = true

        if (phoneNum.isEmpty() || phoneNum.isBlank()){
            editTxtReceiverNumber.error = getText(R.string.receiverNumber)
            editTxtReceiverNumber.requestFocus()
            valid = false
        } else if (TextUtils.isDigitsOnly(phoneNum) || !Patterns.PHONE.matcher(phoneNum).matches()){
            editTxtReceiverNumber.error = getText(R.string.validPhone)
            editTxtReceiverNumber.requestFocus()
            valid = false
        } else if (smsMessage.isEmpty() || smsMessage.isBlank()){
            editTxtMessage.error = getText(R.string.messageRequired)
            editTxtMessage.requestFocus()
            valid = false
        } else if (editTxtDate.text.toString().trim().isEmpty()){
            Toast.makeText(this@EditScheduleMessageActivity, R.string.enterValidDate, Toast.LENGTH_SHORT).show()
            valid = false
        } else if (editTxtTime.text.toString().trim().isEmpty()){
            Toast.makeText(this@EditScheduleMessageActivity, R.string.enterValidTime, Toast.LENGTH_SHORT).show()
            valid = false
        }

        return valid
    }

    fun setSMSAlarm(smsId: String, receiverName: String, receiverNumber: String, SMSMessage: String,
                    date: String, time: String, status: String, type: String, currentUser: String,
                    calendar: Long) {

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this@EditScheduleMessageActivity, AlertReceiver::class.java)
        intent.putExtra("SmsId", smsId)
        intent.putExtra("SmsReceiverName", receiverName)
        intent.putExtra("SmsReceiverNumber", receiverNumber)
        intent.putExtra("SmsMsg", SMSMessage)
        intent.putExtra("SmsDate", date)
        intent.putExtra("SmsTime", time)
        intent.putExtra("SmsStatus", status)
        intent.putExtra("SmsType", type)
        intent.putExtra("UserID", currentUser)
        intent.putExtra("calendar", calendar)

        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this@EditScheduleMessageActivity, smsId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (calendarAlarm == null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar, pendingIntent)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendarAlarm!!.timeInMillis, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarAlarm!!.timeInMillis, pendingIntent)
                }
            }
        } else {
            if (calendarAlarm == null){
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendarAlarm!!.timeInMillis, pendingIntent)
            }
        }

        Toast.makeText(this@EditScheduleMessageActivity, R.string.messageSchedule, Toast.LENGTH_SHORT).show()
        finish()
    }

    fun setWhatsAppMessageAlarm(smsId: String, receiverName: String, receiverNumber: String, SMSMessage: String,
                                date: String, time: String, status: String, type: String, currentUser: String, calendar: Long) {

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this@EditScheduleMessageActivity, AlertReceiver::class.java)
        intent.putExtra("SmsId", smsId)
        intent.putExtra("SmsReceiverName", receiverName)
        intent.putExtra("SmsReceiverNumber", receiverNumber)
        intent.putExtra("SmsMsg", SMSMessage)
        intent.putExtra("SmsDate", date)
        intent.putExtra("SmsTime", time)
        intent.putExtra("SmsStatus", status)
        intent.putExtra("SmsType", type)
        intent.putExtra("UserID", currentUser)
        intent.putExtra("calendar", calendar)

        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this@EditScheduleMessageActivity, smsId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (calendarAlarm == null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar, pendingIntent)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendarAlarm!!.timeInMillis, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarAlarm!!.timeInMillis, pendingIntent)
                }
            }
        } else {
            if (calendarAlarm == null){
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendarAlarm!!.timeInMillis, pendingIntent)
            }
        }

        Toast.makeText(this@EditScheduleMessageActivity, R.string.messageSchedule, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            PERMISSION_REQUEST_SEND_SMS -> {
                if (grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    editScheduleSMSMessage()
                } else {
                    Toast.makeText(this@EditScheduleMessageActivity, R.string.sendMessagePermission, Toast.LENGTH_LONG).show()
                }
            }

            PERMISSION_REQUEST_READ_CONTACTS -> {
                if (grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readContacts()
                } else {
                    Toast.makeText(this@EditScheduleMessageActivity, R.string.readContactsPermission, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun isAccessibilityOn(context: Context, clazz: Class<out AccessibilityService>): Boolean {
        var accessibilityEnabled = 0
        val service = context.packageName + "/" + clazz.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.applicationContext.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (ignored: Settings.SettingNotFoundException) {

        }

        val colonSplitter = TextUtils.SimpleStringSplitter(':')

        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(context.applicationContext.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)

            if (settingValue != null) {
                colonSplitter.setString(settingValue)
                while (colonSplitter.hasNext()) {
                    val accessibilityService = colonSplitter.next()

                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }

        return false
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
                AlertDialog.Builder(this@EditScheduleMessageActivity)
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
        mInterstitialAd.adUnitId = getString(R.string.interstitialAdId)
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
                Toast.makeText(this@EditScheduleMessageActivity, getString(R.string.welcomeBack), Toast.LENGTH_SHORT).show()
                // Load the next interstitial.
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        // 2. Request an ad
        // 2. Request an ad
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        // 3. Wait until the right moment
    }

    inner class EditScheduleMessageViewModelFactory(private val editScheduleMessageActivity: EditScheduleMessageActivity) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EditScheduleMessageViewModel(editScheduleMessageActivity) as T
        }
    }
}
