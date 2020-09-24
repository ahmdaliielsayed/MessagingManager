package com.message.messagingmanager.view.activity.messages

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import java.util.ArrayList

class SmsRepository(application: Application) {

    private var mAllSms: MutableLiveData<List<SmsEntity>> = MutableLiveData()
    private var mAppContext: Context = application

    fun getAllWords(): MutableLiveData<List<SmsEntity>> {
        val loadSms = LoadSms()
        loadSms.execute()
        return mAllSms
    }

    fun insert(smsEntity: SmsEntity?) {
        insertAsyncTask().execute(smsEntity)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class insertAsyncTask : AsyncTask<SmsEntity?, Void?, Void?>() {
        override fun doInBackground(vararg params: SmsEntity?): Void? {
            val newUri: Uri
            val newValues = ContentValues()
            newValues.put(AppConstants.KEY_MSG_BODY, params[0]!!.getMsg())
            newValues.put(AppConstants.KEY_DATE, params[0]!!.getDate())
            newValues.put(AppConstants.KEY_ADDRESS, params[0]!!.getAddress())
            newUri = mAppContext.contentResolver.insert(
                Uri.parse("content://sms/sent"),
                newValues
            )!!
            return null
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class LoadSms : AsyncTask<String?, Void?, List<SmsEntity>>() {

        var mAdapterList: MutableList<SmsEntity> = ArrayList()

        override fun onPreExecute() {
            super.onPreExecute()
            mAdapterList.clear()
        }

        @SuppressLint("Recycle")
        override fun doInBackground(vararg params: String?): List<SmsEntity>? {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(mAppContext, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                try {
                    val uriInbox = Uri.parse("content://sms/")
                    val c: Cursor = mAppContext.contentResolver.query(
                        uriInbox,
                        null,
                        "address IS NOT NULL) GROUP BY (thread_id",
                        null,
                        null
                    )!! // 2nd null = "address IS NOT NULL) GROUP BY (address"
//                Uri uriSent = Uri.parse("content://sms/sent");
//                Cursor sent = getContentResolver().query(uriSent, null, "address IS NOT NULL) GROUP BY (thread_id", null, null); // 2nd null = "address IS NOT NULL) GROUP BY (address"
//                Cursor c = new MergeCursor(new Cursor[]{inbox,sent}); // Attaching inbox and sent sms
                    if (c.moveToFirst()) {
                        for (i in 0 until c.count) {
                            val _id = c.getString(c.getColumnIndexOrThrow(AppConstants._ID))
                            val thread_id = c.getString(c.getColumnIndexOrThrow(AppConstants.KEY_THREAD_ID))
                            val msg = c.getString(c.getColumnIndexOrThrow(AppConstants.KEY_MSG_BODY))
                            val type = c.getString(c.getColumnIndexOrThrow(AppConstants.KEY_TYPE))
                            val date = c.getString(c.getColumnIndexOrThrow(AppConstants.KEY_DATE))
                            val user = c.getString(c.getColumnIndexOrThrow(AppConstants.KEY_ADDRESS))
                            mAdapterList.add(SmsEntity(_id, thread_id, user, type, date, msg))
                            c.moveToNext()
                        }
                    }
                    c.close()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }

//            Collections.sort(smsList, new MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging sms by timestamp decending
//            ArrayList<HashMap<String, String>> purified = Function.removeDuplicates(smsList); // Removing duplicates from inbox & sent
//            smsList.clear();
//            smsList.addAll(purified);
            return mAdapterList
        }

        override fun onPostExecute(list: List<SmsEntity>) {
            mAllSms.postValue(list)
        }
    }
}