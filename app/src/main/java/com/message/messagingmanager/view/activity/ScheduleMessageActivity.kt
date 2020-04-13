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
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
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

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_message)

        scheduleMessageViewModel = ViewModelProviders.of(this, ScheduleMessageViewModelFactory(this@ScheduleMessageActivity))
            .get(ScheduleMessageViewModel::class.java)

        name = intent.getStringExtra("SmsReceiverName")
        phone = intent.getStringExtra("SmsReceiverNumber")
        spinnerValue = intent.getStringExtra("spinnerValue")

        txtViewPersonName.text = name
        editTxtReceiverNumber.setText(phone)

        toolbar.title = "Schedule Messages"
        setSupportActionBar(toolbar)

        /*** date ***/
        editTxtDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(this@ScheduleMessageActivity,
                calender, year, month, day)
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
                    myDateCheck!!.before(date) -> Toast.makeText(this@ScheduleMessageActivity,
                        "Please, Enter a valid Date!", Toast.LENGTH_LONG).show()
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
                        Toast.makeText(this@ScheduleMessageActivity,
                            "Please, Enter a valid Time!", Toast.LENGTH_LONG).show()
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
            val permissionCheck: Int = ContextCompat.checkSelfPermission(this@ScheduleMessageActivity, Manifest.permission.SEND_SMS)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED){
                scheduleSMSMessage()
            } else {
                ActivityCompat.requestPermissions(this@ScheduleMessageActivity, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_REQUEST_SEND_SMS)
            }
        }

        btnWhatsApp.setOnClickListener {
            if (isAccessibilityOn(this@ScheduleMessageActivity, WhatsappAccessibilityService::class.java)) {
                if (txtViewPersonName.text.toString().trim() == "No Name!") {
                    AlertDialog.Builder(this@ScheduleMessageActivity)
                        .setTitle("Select Receiver")
                        .setMessage("You must select contact from the image on top of this screen not typing his info!")
                        .setIcon(R.drawable.cancel)
                        .setPositiveButton("Ok") { _, _ ->
                            val permissionCheck: Int = ContextCompat.checkSelfPermission(this@ScheduleMessageActivity, Manifest.permission.READ_CONTACTS)
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED){
                                startActivity(Intent(this@ScheduleMessageActivity, ContactsActivity::class.java))
                                finish()
                            } else {
                                ActivityCompat.requestPermissions(this@ScheduleMessageActivity, arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_READ_CONTACTS)
                            }
                        }
                        .show()
                } else {
                    scheduleWhatsAppMessage()
                }
            } else {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
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
                        Toast.makeText(this@ScheduleMessageActivity,
                            "You will see all contacts now if you press on the image above!",
                            Toast.LENGTH_LONG).show()
                        spinnerValue = "SMS"
                        btnSMS.visibility = View.VISIBLE
                        btnWhatsApp.visibility = View.GONE
                    }
                    1 -> {
                        Toast.makeText(this@ScheduleMessageActivity,
                            "You will see only contacts that have WhatsApp now if you press on the image above!",
                            Toast.LENGTH_LONG).show()
                        spinnerValue = "WhatsApp"
                        btnSMS.visibility = View.GONE
                        btnWhatsApp.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

        btnAdvancedOptions.setOnClickListener {
            startActivity(Intent(this@ScheduleMessageActivity, SelectSIMActivity::class.java))
            finish()
        }
    }

    private fun scheduleSMSMessage(){

        val receiverNumber: String
        if (validateInputs()){

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
            editTxtReceiverNumber.error = "Receiver Phone Number Required!"
            editTxtReceiverNumber.requestFocus()
            valid = false
        } else if (TextUtils.isDigitsOnly(phoneNum) || !Patterns.PHONE.matcher(phoneNum).matches()){
            editTxtReceiverNumber.error = "Enter a valid phone number!\nLIKE: 0106 320 8399\nLIKE: +201063208399\nor choose from your contacts by pressing on image on top of this screen!"
            editTxtReceiverNumber.requestFocus()
            valid = false
        } else if (smsMessage.isEmpty() || smsMessage.isBlank()){
            editTxtMessage.error = "Message mustn't be empty!"
            editTxtMessage.requestFocus()
            valid = false
        } else if (editTxtDate.text.toString().trim().isEmpty()){
            Toast.makeText(this@ScheduleMessageActivity, "Enter a valid Date!", Toast.LENGTH_SHORT).show()
            valid = false
        } else if (editTxtTime.text.toString().trim().isEmpty()){
            Toast.makeText(this@ScheduleMessageActivity, "Enter a valid Time!", Toast.LENGTH_SHORT).show()
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
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarAlarm.timeInMillis, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendarAlarm.timeInMillis, pendingIntent)
        }

        Toast.makeText(this@ScheduleMessageActivity, "Message scheduled successfully!", Toast.LENGTH_SHORT).show()
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
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarAlarm.timeInMillis, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendarAlarm.timeInMillis, pendingIntent)
        }

        Toast.makeText(this@ScheduleMessageActivity, "Message scheduled successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            PERMISSION_REQUEST_SEND_SMS -> {
                if (grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    scheduleSMSMessage()
                } else {
                    Toast.makeText(this@ScheduleMessageActivity, "Give us the permission to send your messages!", Toast.LENGTH_LONG).show()
                }
            }

            PERMISSION_REQUEST_READ_CONTACTS -> {
                if (grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readContacts()
                } else {
                    Toast.makeText(this@ScheduleMessageActivity, "Give us the permission to read your contacts!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun isAccessibilityOn(context: Context, clazz: Class<out AccessibilityService>): Boolean {
        var accessibilityEnabled = 0
        val service = context.packageName + "/" + clazz.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (ignored: Settings.SettingNotFoundException) {

        }

        val colonSplitter = TextUtils.SimpleStringSplitter(':')

        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(context.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )

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

    inner class ScheduleMessageViewModelFactory(private val scheduleMessageActivity: ScheduleMessageActivity) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ScheduleMessageViewModel(scheduleMessageActivity) as T
        }
    }
}
