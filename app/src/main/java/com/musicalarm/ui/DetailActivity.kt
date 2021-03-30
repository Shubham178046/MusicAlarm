package com.musicalarm.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.musicalarm.R
import com.musicalarm.db.DatabaseHelper
import com.musicalarm.model.Alarm
import com.musicalarm.model.AlarmSong
import com.musicalarm.service.AlarmReceiver
import com.musicalarm.service.LoadAlarmsService
import com.musicalarm.util.FileUtils
import com.musicalarm.util.ViewUtil
import kotlinx.android.synthetic.main.activity_detail.*
import java.io.File
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

class DetailActivity : AppCompatActivity() {
    final @Mode
    var modes = UNKNOWN
    var alarmSong: AlarmSong = AlarmSong()
    var alarmData: Alarm? = null
    var SONG: String? = ""
    var day: Int = -1

    companion object {
        const val ALARM_EXTRA = "alarm_extra"
        const val MODE_EXTRA = "mode_extras"
        const val DAY = "day_extra"

        @Retention(RetentionPolicy.SOURCE)
        @IntDef(EDIT_ALARM, ADD_ALARM, UNKNOWN)
        internal annotation class Mode

        const val EDIT_ALARM: Int = 1
        const val ADD_ALARM: Int = 2
        const val UNKNOWN: Int = 0
        fun buildAddEditAlarmActivityIntent(
            context: Context?,
            @Mode mode: Int,
            alarm: Alarm,
            day: Int
        ): Intent? {
            val i =
                Intent(context, DetailActivity::class.java)
            i.putExtra(ALARM_EXTRA, alarm)
            i.putExtra(MODE_EXTRA, mode)
            i.putExtra(DAY, day)
            return i
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initViews()
        actionListner()
    }

    private fun actionListner() {
        btnSave.setOnClickListener {
            if (txtMusic.equals(resources.getString(R.string.select_music))) {
                txtMusic.setError(getString(R.string.please_select_music))
                return@setOnClickListener
            }
            save()
            //Toast.makeText(this, getString(R.string.save_successfully), Toast.LENGTH_LONG).show()
            // onBackPressed()
        }
        txtMusic.setOnClickListener {
            val random = Random()
            var intent = Intent(this, MusicListActivity::class.java)
            intent.putExtra("day", random.nextInt(100))
            startActivityForResult(intent, 101)
        }
    }

    private fun save() {
        var alarm: Alarm?
        if (alarmData != null) {
            alarm = alarmData
        } else {
            alarm = getAlarm()
        }
        val time = Calendar.getInstance()
        time[Calendar.MINUTE] = ViewUtil.getTimePickerMinute(edit_alarm_time_picker)
        time[Calendar.HOUR_OF_DAY] = ViewUtil.getTimePickerHour(edit_alarm_time_picker)
        time[Calendar.SECOND] = 0
        println("Time And TimeMills" + time.time + "*********" + time.timeInMillis)
        alarm!!.time = time.timeInMillis
        alarm.label = edtLabel.getText().toString()
        alarm.setDay(day, true)
        alarm.day_id = day
        alarm.setALARM_SONG(SONG)
        val rowsUpdated = DatabaseHelper.getInstance(this).updateAlarm(alarm)
        val messageId: Int =
            if (rowsUpdated == 1) R.string.update_complete else R.string.update_failed
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
        AlarmReceiver.setReminderAlarm(this, alarm)
        finish()
    }

    private fun initViews() {
        modes = intent.getIntExtra(MODE_EXTRA, UNKNOWN)
        day = intent.getIntExtra(DAY, UNKNOWN)
        val alarm = getAlarm()
        ViewUtil.setTimePickerTime(edit_alarm_time_picker, alarm!!.time)
        if(alarm.alarM_SONG != null && !alarm.alarM_SONG.equals("") && !alarm.alarM_SONG.isNullOrEmpty())
        {
            SONG = alarm.alarM_SONG
        }
        edtLabel.setText(alarm.label)
        if (alarm.alarM_SONG != null) {
            if (!alarm.alarM_SONG.equals("") && !alarm.alarM_SONG.isNullOrEmpty()) {
                txtMusic.setText(FileUtils.getFileName(this, Uri.fromFile(File(alarm.alarM_SONG))))
            }
        }
    }

    private fun getAlarm(): Alarm? {
        when (modes) {
            EDIT_ALARM -> {
                alarmData = getIntent().getParcelableExtra(ALARM_EXTRA)!!
                return getIntent().getParcelableExtra(ALARM_EXTRA)
            }

            ADD_ALARM -> {
                val id: Long = DatabaseHelper.getInstance(this).addAlarm()
                LoadAlarmsService.launchLoadAlarmsService(this, day)
                alarmData = Alarm(id)
                return Alarm(id)
            }
            UNKNOWN -> throw IllegalStateException(
                "Mode supplied as intent extra for " +
                        "TAG" + " must match value in " +
                        DetailActivity.Companion.Mode::class.java.getSimpleName()
            )
            else -> throw IllegalStateException(
                ("Mode supplied as intent extra for " +
                        "TAG" + " must match value in " +
                        DetailActivity.Companion.Mode::class.java.getSimpleName())
            )
        }
    }

    private fun delete() {
        val alarm = getAlarm()
        val builder: AlertDialog.Builder = AlertDialog.Builder(
            this,
            R.style.DeleteAlarmDialogTheme
        )
        builder.setTitle(R.string.delete_dialog_title)
        builder.setMessage(R.string.delete_dialog_content)
        builder.setPositiveButton(R.string.yes,
            DialogInterface.OnClickListener { dialogInterface, i -> //Cancel any pending notifications for this alarm
                AlarmReceiver.cancelReminderAlarm(this, alarm!!)
                val rowsDeleted = DatabaseHelper.getInstance(this).deleteAlarm(alarm)
                val messageId: Int
                if (rowsDeleted == 1) {
                    messageId = R.string.delete_complete
                    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
                    LoadAlarmsService.launchLoadAlarmsService(this, day)
                    //   getActivity().finish()
                } else {
                    messageId = R.string.delete_failed
                    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
                }
            })
        builder.setNegativeButton(R.string.no, null)
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            alarmSong = data?.getSerializableExtra("alarmSong") as AlarmSong
            txtMusic.setText(alarmSong.title)
            SONG = alarmSong.uri
            // alarmSongList.add(alarmSong)
            //uri = data?.getStringExtra("uri")
        }

    }
}