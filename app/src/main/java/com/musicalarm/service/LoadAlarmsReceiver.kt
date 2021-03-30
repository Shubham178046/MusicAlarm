package com.musicalarm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.musicalarm.model.Alarm
import java.util.*

class LoadAlarmsReceiver(val mListener: OnAlarmsLoadedListener) : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        val alarms: ArrayList<Alarm>? = intent!!.getParcelableArrayListExtra<Alarm>(LoadAlarmsService.ALARMS_EXTRA)

        mListener!!.onAlarmsLoaded(alarms)
    }
    interface OnAlarmsLoadedListener {
        fun onAlarmsLoaded(alarms: ArrayList<Alarm>?)
    }
}