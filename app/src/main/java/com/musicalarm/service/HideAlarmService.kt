package com.musicalarm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.musicalarm.helper.hideNotification
import com.musicalarm.helper.stopAlarmSound
import com.musicalarm.util.Constants.ALARM_ID

class HideAlarmService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent!!.getIntExtra(ALARM_ID, -1)
        context!!.hideNotification(id)
        context.stopAlarmSound()
    }
}