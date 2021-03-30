package com.musicalarm.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.musicalarm.R
import com.musicalarm.model.Alarm.*
import com.musicalarm.ui.DetailActivity.Companion.DAY
import kotlinx.android.synthetic.main.activity_days.*

class DaysActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_days)
        actionListner()
    }

    private fun actionListner() {
        llSunday.setOnClickListener {
            var intent : Intent = Intent(this, DaywiseAlarmActivity::class.java)
            intent.putExtra(DAY,SUN)
            startActivity(intent)
        }

        llMonday.setOnClickListener {
            var intent : Intent = Intent(this, DaywiseAlarmActivity::class.java)
            intent.putExtra(DAY,MON)
            startActivity(intent)
        }

        llTuesday.setOnClickListener {
            var intent : Intent = Intent(this, DaywiseAlarmActivity::class.java)
            intent.putExtra(DAY,TUES)
            startActivity(intent)
        }

        llWednesday.setOnClickListener {
            var intent : Intent = Intent(this, DaywiseAlarmActivity::class.java)
            intent.putExtra(DAY, WED)
            startActivity(intent)
        }

        llThursday.setOnClickListener {
            var intent : Intent = Intent(this, DaywiseAlarmActivity::class.java)
            intent.putExtra(DAY, THURS)
            startActivity(intent)
        }

        llFriday.setOnClickListener {
            var intent : Intent = Intent(this, DaywiseAlarmActivity::class.java)
            intent.putExtra(DAY, FRI)
            startActivity(intent)
        }

        llSaturday.setOnClickListener {
            var intent : Intent = Intent(this, DaywiseAlarmActivity::class.java)
            intent.putExtra(DAY, SAT)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        finishAffinity()
    }
}