package com.musicalarm.ui

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.musicalarm.R
import com.musicalarm.adapter.DaywiseAlarmAdapter
import com.musicalarm.db.DatabaseHelper
import com.musicalarm.model.Alarm
import com.musicalarm.service.LoadAlarmsReceiver
import com.musicalarm.service.LoadAlarmsService
import com.musicalarm.ui.DetailActivity.Companion.ADD_ALARM
import com.musicalarm.ui.DetailActivity.Companion.DAY
import com.musicalarm.ui.DetailActivity.Companion.buildAddEditAlarmActivityIntent
import kotlinx.android.synthetic.main.activity_daywise_alarm.*
import java.util.ArrayList

class DaywiseAlarmActivity : AppCompatActivity(), LoadAlarmsReceiver.OnAlarmsLoadedListener {
    var day: Int? = -1
    private var mReceiver: LoadAlarmsReceiver? = null
    private var mAdapter: DaywiseAlarmAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daywise_alarm)
        initViews()
        actionListner()
    }

    private fun initViews() {
        day = intent.getIntExtra(DAY, -1)
        mReceiver = LoadAlarmsReceiver(this)
        rvAlarm.layoutManager = LinearLayoutManager(this)
        rvAlarm.itemAnimator = DefaultItemAnimator()
        mAdapter = DaywiseAlarmAdapter(this, day!!)
        var adp = mAdapter
        rvAlarm.adapter = adp
        val alarms: List<Alarm>? = DatabaseHelper.getInstance(this).getAlarms()
        if(alarms != null)
        {
            if(alarms.size > 0)
            {
                for (i in 0 until alarms.size) {
                    println(
                        "Data----->" + alarms.get(i).id + " " + alarms.get(i).alarM_SONG + " " + alarms.get(
                            i
                        ).day_id
                    )
                }
            }
        }


    }

    private fun actionListner() {
        fbAdd.setOnClickListener {
            val intent = buildAddEditAlarmActivityIntent(this, ADD_ALARM, Alarm(), day!!)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(LoadAlarmsService.ACTION_COMPLETE)
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver!!, filter)
        LoadAlarmsService.launchLoadAlarmsService(this, day!!)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver!!)
    }

    override fun onAlarmsLoaded(alarms: ArrayList<Alarm>?) {
        mAdapter!!.setAlarms(alarms!!)
    }

}