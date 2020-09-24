package com.message.messagingmanager.view.activity.messages

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object{
        fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission!!) != PackageManager.PERMISSION_GRANTED) {
                        return false
                    }
                }
            }
            return true
        }

        fun showSnackBar(view: View?, message: String?) {
            Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }

        fun showToast(context: Context?, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        @SuppressLint("SimpleDateFormat")
        fun getTimeStamp(timestamp: String): String? {
            return if (!TextUtils.isEmpty(timestamp)) {
                val datetime = timestamp.toLong()
                val date = Date(datetime)
                val formatter: DateFormat = SimpleDateFormat("dd/MM/YYYY HH:mm")
                formatter.format(date)
            } else {
                "--"
            }
        }
    }
}