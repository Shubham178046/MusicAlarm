package com.musicalarm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.musicalarm.db.DatabaseHelper
import com.musicalarm.model.Alarm
import com.musicalarm.service.AlarmReceiver.Companion.setReminderAlarms
import java.util.concurrent.Executors

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent!!.getAction()) {
            Executors.newSingleThreadExecutor().execute {
                val alarms: List<Alarm> = DatabaseHelper.getInstance(context).getAlarms()
                setReminderAlarms(context, alarms)
            }
        }
    }
}