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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.message.messagingmanager.HomeActivity
import com.message.messagingmanager.R
import com.message.messagingmanager.WhatsappAccessibilityService
import com.message.messagingmanager.model.Contact
import com.message.messagingmanager.model.Message
import com.message.messagingmanager.view.receiver.AlertReceiver
import kotlinx.android.synthetic.main.activity_schedule_group_message.*
import kotlinx.android.synthetic.main.app_bar.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ScheduleGroupMessageActivity : AppCompatActivity() {

    private var arrContacts: ArrayList<Contact> = ArrayList()

    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var databaseReferenceContacts: DatabaseReference
    private var databaseReferenceMsg: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Messages")

    private val PERMISSION_REQUEST_SEND_SMS = 1

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

    private lateinit var calendarAlarm: Calendar

    private var groupID: String = ""
    private var groupName: String = ""

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onStart() {
        super.onStart()

        groupID = intent.getStringExtra("groupID")

        progressBar.visibility = View.VISIBLE
        databaseReferenceContacts = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Groups").child(groupID).child("Contacts")
        databaseReferenceContacts.keepSynced(true)
        databaseReferenceContacts.addValueEventListener(object : ValueEventListener {
            @SuppressLint("WrongConstant")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                arrContacts.clear()

                for (msgSnapshot in dataSnapshot.children) {
                    val contact = msgSnapshot.getValue(Contact::class.java)

                    if (contact!!.getGroupId() == groupID) {
                        arrContacts.add(contact)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
        progressBar.visibility = View.GONE
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_group_message)

        groupName = intent.getStringExtra("groupName")
        txtViewGroupName.text = groupName

        toolbar.setTitle(R.string.scheduleMessages)
        setSupportActionBar(toolbar)

        /*** date ***/
        editTxtDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(
                this@ScheduleGroupMessageActivity,
                calender, year, month, day
            )
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
                myDateCheck!!.before(date) -> Toast.makeText(
                    this@ScheduleGroupMessageActivity, R.string.enterValidDate, Toast.LENGTH_LONG
                ).show()
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

            timePickerDialog = TimePickerDialog(
                this@ScheduleGroupMessageActivity,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
                    val myCalInstance = Calendar.getInstance()
                    val myRealCalender = Calendar.getInstance()

                    if (myDateCheck == null) {
                        val timeStamp =
                            SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)
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
                        Toast.makeText(
                            this@ScheduleGroupMessageActivity, R.string.enterValidTime, Toast.LENGTH_LONG
                        ).show()
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
            val permissionCheck: Int = ContextCompat.checkSelfPermission(
                this@ScheduleGroupMessageActivity,
                Manifest.permission.SEND_SMS
            )
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                scheduleSMSMessage()
            } else {
                ActivityCompat.requestPermissions(
                    this@ScheduleGroupMessageActivity, arrayOf(Manifest.permission.SEND_SMS),
                    this.PERMISSION_REQUEST_SEND_SMS
                )
            }
        }

        btnWhatsApp.setOnClickListener {
            if (isAccessibilityOn(this@ScheduleGroupMessageActivity, WhatsappAccessibilityService::class.java)) {
                scheduleWhatsAppMessage()
            } else {
                AlertDialog.Builder(this@ScheduleGroupMessageActivity)
                    .setTitle(R.string.enableAccessibility)
                    .setMessage(R.string.accessibilitySteps)
                    .setIcon(R.drawable.ic_check_circle_green_24dp)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                    .show()
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

    private fun scheduleSMSMessage() {

        if (validateInputs()) {
            progressBar.visibility = View.VISIBLE
            var calenderValue = calendarAlarm.timeInMillis

            for (item in arrContacts.indices) {
                val smsId: String = databaseReferenceMsg.push().key.toString()
                val message = Message(
                    smsId,
                    arrContacts[item].getContactName(),
                    arrContacts[item].getContactNumber(),
                    editTxtMessage.text.toString().trim(),
                    editTxtDate.text.toString().trim(),
                    editTxtTime.text.toString().trim(),
                    resources.getString(R.string.status_upcoming),
                    resources.getString(R.string.type_sms),
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    calenderValue
                )
                databaseReferenceMsg.child(smsId).setValue(message)

                setSMSAlarm(
                    smsId,
                    arrContacts[item].getContactName(),
                    arrContacts[item].getContactNumber(),
                    editTxtMessage.text.toString().trim(),
                    editTxtDate.text.toString().trim(),
                    editTxtTime.text.toString().trim(),
                    resources.getString(R.string.status_upcoming),
                    resources.getString(R.string.type_sms),
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    calenderValue
                )

                calenderValue += 10000
            }

            startActivity(Intent(this@ScheduleGroupMessageActivity, HomeActivity::class.java))
            finish()
        }
    }

    private fun scheduleWhatsAppMessage() {

        if (validateInputs()) {
            progressBar.visibility = View.VISIBLE
            var calenderValue = calendarAlarm.timeInMillis

            for (item in arrContacts.indices) {
                val smsId: String = databaseReferenceMsg.push().key.toString()
                val message = Message(
                    smsId,
                    arrContacts[item].getContactName(),
                    arrContacts[item].getContactNumber(),
                    editTxtMessage.text.toString().trim(),
                    editTxtDate.text.toString().trim(),
                    editTxtTime.text.toString().trim(),
                    resources.getString(R.string.status_upcoming),
                    resources.getString(R.string.type_whats_app),
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    calenderValue
                )
                databaseReferenceMsg.child(smsId).setValue(message)

                setWhatsAppMessageAlarm(
                    smsId,
                    arrContacts[item].getContactName(),
                    arrContacts[item].getContactNumber(),
                    editTxtMessage.text.toString().trim(),
                    editTxtDate.text.toString().trim(),
                    editTxtTime.text.toString().trim(),
                    resources.getString(R.string.status_upcoming),
                    resources.getString(R.string.type_whats_app),
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    calenderValue
                )

                calenderValue += 10000
            }

            startActivity(Intent(this@ScheduleGroupMessageActivity, HomeActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        val smsMessage: String = editTxtMessage.text.toString().trim()

        var valid = true

        if (smsMessage.isEmpty() || smsMessage.isBlank()) {
            editTxtMessage.error = getText(R.string.messageRequired)
            editTxtMessage.requestFocus()
            valid = false
        } else if (editTxtDate.text.toString().trim().isEmpty()) {
            Toast.makeText(this@ScheduleGroupMessageActivity, R.string.enterValidDate, Toast.LENGTH_SHORT).show()
            valid = false
        } else if (editTxtTime.text.toString().trim().isEmpty()) {
            Toast.makeText(this@ScheduleGroupMessageActivity, R.string.enterValidTime, Toast.LENGTH_SHORT).show()
            valid = false
        }

        return valid
    }

    private fun setSMSAlarm(
        smsId: String, receiverName: String, receiverNumber: String, SMSMessage: String,
        date: String, time: String, status: String, type: String, currentUser: String,
        calendar: Long
    ) {

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this@ScheduleGroupMessageActivity, AlertReceiver::class.java)
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
            PendingIntent.getBroadcast(
                this@ScheduleGroupMessageActivity,
                smsId.hashCode(),
                intent,
                0
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar, pendingIntent)
        }

        Toast.makeText(this@ScheduleGroupMessageActivity, R.string.messageSchedule, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun setWhatsAppMessageAlarm(
        smsId: String,
        receiverName: String,
        receiverNumber: String,
        SMSMessage: String,
        date: String,
        time: String,
        status: String,
        type: String,
        currentUser: String,
        calendar: Long
    ) {

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this@ScheduleGroupMessageActivity, AlertReceiver::class.java)
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
            PendingIntent.getBroadcast(
                this@ScheduleGroupMessageActivity,
                smsId.hashCode(),
                intent,
                0
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar, pendingIntent)
        }

        Toast.makeText(this@ScheduleGroupMessageActivity, R.string.messageSchedule, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_SEND_SMS -> {
                if (grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scheduleSMSMessage()
                } else {
                    Toast.makeText(this@ScheduleGroupMessageActivity, R.string.sendMessagePermission, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
