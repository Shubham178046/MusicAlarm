package com.musicalarm.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.musicalarm.db.DatabaseHelper
import com.musicalarm.model.Alarm
import com.musicalarm.ui.DetailActivity.Companion.DAY
import java.util.*

class LoadAlarmsService : IntentService {
    constructor() : this(TAG)
    constructor(name: String) : super(name)
    override fun onHandleIntent(p0: Intent?) {
    //    val alarms: List<Alarm> = DatabaseHelper.getInstance(this).getAlarms()
        val alarms: List<Alarm> = DatabaseHelper.getInstance(this).getAlarmAsPerDay(
            p0!!.getIntExtra(
                DAY, -1
            )
        )

        val i = Intent(ACTION_COMPLETE)
        i.putParcelableArrayListExtra(ALARMS_EXTRA, ArrayList<Alarm>(alarms))
        LocalBroadcastManager.getInstance(this).sendBroadcast(i)
    }

    companion object {
        private val TAG = LoadAlarmsService::class.java.simpleName
        val ACTION_COMPLETE = "$TAG.ACTION_COMPLETE"
        const val ALARMS_EXTRA = "alarms_extra"
        fun launchLoadAlarmsService(context: Context, dayid: Int) {
            val launchLoadAlarmsServiceIntent = Intent(context, LoadAlarmsService::class.java)
            launchLoadAlarmsServiceIntent.putExtra(DAY, dayid)
            context.startService(launchLoadAlarmsServiceIntent)
        }
    }
}