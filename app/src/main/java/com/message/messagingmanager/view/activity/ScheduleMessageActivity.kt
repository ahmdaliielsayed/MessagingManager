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
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.message.messagingmanager.viewmodel.ScheduleMessageViewModel
import kotlinx.android.synthetic.main.activity_schedule_message.*
import kotlinx.android.synthetic.main.app_bar.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ScheduleMessageActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_SEND_SMS = 1
    private val PERMISSION_REQUEST_READ_CONTACTS = 2

    private var counterReadContacts = 0
    private var counterSendSMS = 0

    private var calender: DatePickerDialog.OnDateSetListener? = null
    private var timePickerDialog: TimePickerDialog? = null

    private var date: Date? = null
    private var myDateCheck:Date? = null
    private var mHour = 0
    private var mMin = 0
    private var hours1 = 0
    private var min1 = 0
    private var year1 = 0
    private var month1 = 0
    private var dayOfMonth1 = 0

    private lateinit var calendarAlarm: Calendar

    private var name: String = ""
    private var phone: String = ""
    private var spinnerValue: String = ""

    private lateinit var scheduleMessageViewModel: ScheduleMessageViewModel

    private lateinit var adView: AdView
    private lateinit var mInterstitialAd: InterstitialAd

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_message)

        scheduleMessageViewModel = ViewModelProviders.of(this, ScheduleMessageViewModelFactory(this@ScheduleMessageActivity))
            .get(ScheduleMessageViewModel::class.java)

        makeAd()
        prepareInterstitialAd()

        name = intent.getStringExtra("SmsReceiverName")
        phone = intent.getStringExtra("SmsReceiverNumber")
        spinnerValue = intent.getStringExtra("spinnerValue")

        txtViewPersonName.text = name
        editTxtReceiverNumber.setText(phone)

        toolbar.setTitle(R.string.scheduleMessages)
        setSupportActionBar(toolbar)

        /*** date ***/
        editTxtDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(this@ScheduleMessageActivity, calender, year, month, day)
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
                    myDateCheck!!.before(date) -> Toast.makeText(this@ScheduleMessageActivity, R.string.enterValidDate, Toast.LENGTH_LONG).show()
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

            timePickerDialog = TimePickerDialog(this@ScheduleMessageActivity,
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
                        Toast.makeText(this@ScheduleMessageActivity, R.string.enterValidTime, Toast.LENGTH_LONG).show()
                    } else {
                        hours1 = hourOfDay
                        min1 = minutes
                        calendarAlarm = Calendar.getInstance()
                        calendarAlarm.set(Calendar.YEAR, year1)
                        calendarAlarm.set(Calendar.MONTH, month1 - 1)
                        calendarAlarm.set(Calendar.DAY_OF_MONTH, dayOfMonth1)
                        calendarAlarm.set(Calendar.HOUR_OF_DAY, hours1)
                        calendarAlarm.set(Calendar.MINUTE, min1)
                        calendarAlarm.set(Calendar.SECOND, 0)
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

//            AlertDialog.Builder(this@ScheduleMessageActivity)
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
//                    Toast.makeText(this@ScheduleMessageActivity, R.string.cannotSendSMS, Toast.LENGTH_LONG).show()
//                }
//                .show()

            val permissionCheck: Int = ContextCompat.checkSelfPermission(this@ScheduleMessageActivity, Manifest.permission.SEND_SMS)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED){
                // 4. Check if the ad has loaded
                // 5. Display ad
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
                // scheduleSMSMessage()
            } else {
                ActivityCompat.requestPermissions(this@ScheduleMessageActivity, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_REQUEST_SEND_SMS)
            }
        }

        btnWhatsApp.setOnClickListener {
            if (isAccessibilityOn(this@ScheduleMessageActivity, WhatsappAccessibilityService::class.java)) {
                if (txtViewPersonName.text.toString().trim() == "No Name!") {
                    AlertDialog.Builder(this@ScheduleMessageActivity)
                        .setTitle(R.string.selectReceiver)
                        .setMessage(R.string.selectContact)
                        .setIcon(R.drawable.cancel)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            val permissionCheck: Int = ContextCompat.checkSelfPermission(this@ScheduleMessageActivity, Manifest.permission.READ_CONTACTS)
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED){
                                readContacts()
                            } else {
                                ActivityCompat.requestPermissions(this@ScheduleMessageActivity, arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_READ_CONTACTS)
                            }
                        }
                        .show()
                } else {
//                    // 4. Check if the ad has loaded
//                    // 5. Display ad
//                    if (mInterstitialAd.isLoaded) {
//                        mInterstitialAd.show()
//                    }
                    scheduleWhatsAppMessage()
                }
            } else {
                AlertDialog.Builder(this@ScheduleMessageActivity)
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
            val permissionCheck: Int = ContextCompat.checkSelfPermission(this@ScheduleMessageActivity, Manifest.permission.READ_CONTACTS)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED){
                readContacts()
            } else {
                ActivityCompat.requestPermissions(this@ScheduleMessageActivity, arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_READ_CONTACTS)
            }
        }

        if (spinnerValue == "" || spinnerValue == "SMS") {
            spinner.setSelection(0)
        } else {
            spinner.setSelection(1)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                when (i) {
                    0 -> {
                        Toast.makeText(this@ScheduleMessageActivity, R.string.allContacts, Toast.LENGTH_LONG).show()
                        spinnerValue = "SMS"
                        btnSMS.visibility = View.VISIBLE
                        btnWhatsApp.visibility = View.GONE
                        constraint.visibility = View.GONE
                    }
                    1 -> {
                        Toast.makeText(this@ScheduleMessageActivity, R.string.whatsAppContacts, Toast.LENGTH_LONG).show()
                        spinnerValue = "WhatsApp"
                        btnSMS.visibility = View.GONE
                        btnWhatsApp.visibility = View.VISIBLE
                        constraint.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

        btnAdvancedOptions.setOnClickListener {
            // 4. Check if the ad has loaded
            // 5. Display ad
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            }
            startActivity(Intent(this@ScheduleMessageActivity, SelectSIMActivity::class.java))
            finish()
        }

        txtViewOptimizeBattery.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                // 4. Check if the ad has loaded
//                // 5. Display ad
//                if (mInterstitialAd.isLoaded) {
//                    mInterstitialAd.show()
//                }
//
//                val packageName = packageName
//                val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
//                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//                    val intent = Intent()
//                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    intent.data = Uri.parse("package:$packageName")
//                    startActivity(intent)
//                }
//            } else {
//                Toast.makeText(this, getText(R.string.oldVersion).toString(), Toast.LENGTH_LONG).show()
//            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName = packageName
                val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    AlertDialog.Builder(this@ScheduleMessageActivity)
                        .setTitle(R.string.batteryOptimizationSettings)
                        .setMessage(R.string.turnOffBatteryOptimization)
                        .setIcon(R.drawable.ic_check_circle_green_24dp)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                            intent.data = Uri.parse("package:$packageName")
                            startActivity(intent)
                        }
                        .show()
                } else {
                    Toast.makeText(this@ScheduleMessageActivity, getString(R.string.perfectMsg), Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.oldVersion), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun scheduleSMSMessage(){

        val receiverNumber: String
        if (validateInputs()){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName = packageName
                val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    AlertDialog.Builder(this@ScheduleMessageActivity)
                        .setTitle(R.string.batteryOptimizationSettings)
                        .setMessage(R.string.turnOffBatteryOptimization)
                        .setIcon(R.drawable.ic_check_circle_green_24dp)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                            intent.data = Uri.parse("package:$packageName")
                            startActivity(intent)
                        }
                        .show()
                    return
                } else {
                    Toast.makeText(this@ScheduleMessageActivity, getString(R.string.perfectMsg), Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.oldVersion), Toast.LENGTH_LONG).show()
            }

            receiverNumber = if (editTxtReceiverNumber.text.toString().trim().length == 11) {
                "+2" + editTxtReceiverNumber.text.toString()
            } else if (editTxtReceiverNumber.text.toString().trim().length == 13 && editTxtReceiverNumber.text.toString().trim().contains(" ")) {
                "+2" + editTxtReceiverNumber.text.toString()
            } else {
                editTxtReceiverNumber.text.toString().trim()
            }


            scheduleMessageViewModel.scheduleMessageViewModel(txtViewPersonName.text.toString().trim(),
                receiverNumber, editTxtMessage.text.toString().trim(), editTxtDate.text.toString(),
                editTxtTime.text.toString(), resources.getString(R.string.status_upcoming), resources.getString(R.string.type_sms), calendarAlarm.timeInMillis)
        }
    }

    private fun scheduleWhatsAppMessage(){

        val receiverNumber: String
        if (validateInputs()){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName = packageName
                val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    AlertDialog.Builder(this@ScheduleMessageActivity)
                        .setTitle(R.string.batteryOptimizationSettings)
                        .setMessage(R.string.turnOffBatteryOptimization)
                        .setIcon(R.drawable.ic_check_circle_green_24dp)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                            intent.data = Uri.parse("package:$packageName")
                            startActivity(intent)
                        }
                        .show()
                    return
                } else {
                    Toast.makeText(this@ScheduleMessageActivity, getString(R.string.perfectMsg), Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.oldVersion), Toast.LENGTH_LONG).show()
            }

            receiverNumber = if (editTxtReceiverNumber.text.toString().trim().length == 11) {
                "+2" + editTxtReceiverNumber.text.toString()
            } else if (editTxtReceiverNumber.text.toString().trim().length == 13 && editTxtReceiverNumber.text.toString().trim().contains(" ")) {
                "+2" + editTxtReceiverNumber.text.toString()
            } else {
                editTxtReceiverNumber.text.toString().trim()
            }

            scheduleMessageViewModel.scheduleWhatsAppMessageViewModel(txtViewPersonName.text.toString().trim(),
                receiverNumber, editTxtMessage.text.toString().trim(), editTxtDate.text.toString(),
                editTxtTime.text.toString(), resources.getString(R.string.status_upcoming), resources.getString(R.string.type_whats_app), calendarAlarm.timeInMillis)
        }
    }

    private fun readContacts() {
        progressBar.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imgViewContacts.visibility = View.INVISIBLE
            btnSMS.focusable = View.NOT_FOCUSABLE
            btnWhatsApp.focusable = View.NOT_FOCUSABLE
        }

        val intent = Intent(this@ScheduleMessageActivity, ContactsActivity::class.java)
        intent.putExtra("spinnerValue", spinner.selectedItem.toString())
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
            editTxtReceiverNumber.error = getText(R.string.validPhoneNumber)
            editTxtReceiverNumber.requestFocus()
            valid = false
        } else if (smsMessage.isEmpty() || smsMessage.isBlank()){
            editTxtMessage.error = getText(R.string.messageRequired)
            editTxtMessage.requestFocus()
            valid = false
        } else if (editTxtDate.text.toString().trim().isEmpty()){
            Toast.makeText(this@ScheduleMessageActivity, R.string.enterValidDate, Toast.LENGTH_SHORT).show()
            valid = false
        } else if (editTxtTime.text.toString().trim().isEmpty()){
            Toast.makeText(this@ScheduleMessageActivity, R.string.enterValidTime, Toast.LENGTH_SHORT).show()
            valid = false
        }

        return valid
    }

    fun setSMSAlarm(smsId: String, receiverName: String, receiverNumber: String, SMSMessage: String,
                    date: String, time: String, status: String, type: String, currentUser: String,
                    calendar: Long) {

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this@ScheduleMessageActivity, AlertReceiver::class.java)
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
            PendingIntent.getBroadcast(this@ScheduleMessageActivity, smsId.hashCode(), intent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendarAlarm.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarAlarm.timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendarAlarm.timeInMillis, pendingIntent)
        }

        Toast.makeText(this@ScheduleMessageActivity, R.string.messageSchedule, Toast.LENGTH_SHORT).show()
        finish()
    }

    fun setWhatsAppMessageAlarm(smsId: String, receiverName: String, receiverNumber: String, SMSMessage: String,
                    date: String, time: String, status: String, type: String, currentUser: String, calendar: Long) {

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this@ScheduleMessageActivity, AlertReceiver::class.java)
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
            PendingIntent.getBroadcast(this@ScheduleMessageActivity, smsId.hashCode(), intent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendarAlarm.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarAlarm.timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendarAlarm.timeInMillis, pendingIntent)
        }

        Toast.makeText(this@ScheduleMessageActivity, R.string.messageSchedule, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            PERMISSION_REQUEST_SEND_SMS -> {
                if (grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    scheduleSMSMessage()
                } else {
                    if (counterSendSMS < 2) {
                        Toast.makeText(this@ScheduleMessageActivity, R.string.sendMessagePermission, Toast.LENGTH_LONG).show()
                        counterSendSMS++
                    } else {
                        alertDialog(R.string.permissionRequired, R.string.sendSMSExplanation, R.drawable.warning, R.string.ok)
                    }
                }
            }

            PERMISSION_REQUEST_READ_CONTACTS -> {
                if (grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readContacts()
                } else {
                    if (counterReadContacts < 2) {
                        Toast.makeText(this@ScheduleMessageActivity, R.string.readContactsPermission, Toast.LENGTH_LONG).show()
                        counterReadContacts++
                    } else {
                        alertDialog(R.string.permissionRequired, R.string.readContactsExplanation, R.drawable.warning, R.string.ok)
                    }
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

    private fun alertDialog(title: Int, message: Int, icon: Int, positiveButton: Int) {
        AlertDialog.Builder(this@ScheduleMessageActivity)
            .setTitle(title)
            .setMessage(message)
            .setIcon(icon)
            .setPositiveButton(positiveButton) { _, _ -> }
            .show()
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
                alertDialog(R.string.welcomeBack, R.string.missYou, R.drawable.fire, R.string.ok)
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
                Toast.makeText(this@ScheduleMessageActivity, getString(R.string.welcomeBack), Toast.LENGTH_SHORT).show()
                // Load the next interstitial.
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        // 2. Request an ad
        // 2. Request an ad
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        // 3. Wait until the right moment
    }

    inner class ScheduleMessageViewModelFactory(private val scheduleMessageActivity: ScheduleMessageActivity) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ScheduleMessageViewModel(scheduleMessageActivity) as T
        }
    }
}
