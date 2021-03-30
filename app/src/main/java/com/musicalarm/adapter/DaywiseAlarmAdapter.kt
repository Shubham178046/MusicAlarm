package com.musicalarm.adapter

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.musicalarm.R
import com.musicalarm.db.DatabaseHelper
import com.musicalarm.helper.getfilename
import com.musicalarm.model.Alarm
import com.musicalarm.service.AlarmReceiver
import com.musicalarm.service.LoadAlarmsService
import com.musicalarm.ui.DaywiseAlarmActivity
import com.musicalarm.ui.DetailActivity.Companion.EDIT_ALARM
import com.musicalarm.ui.DetailActivity.Companion.buildAddEditAlarmActivityIntent
import com.musicalarm.util.AlarmUtils
import com.musicalarm.util.FileUtils
import kotlinx.android.synthetic.main.activity_daywise_alarm.*
import kotlinx.android.synthetic.main.item_alarm.view.*
import java.io.File

class DaywiseAlarmAdapter(var context: Context, var day: Int) :
    RecyclerView.Adapter<DaywiseAlarmAdapter.viewVH>() {
    private var mAlarms: ArrayList<Alarm>? = null

    public class viewVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewVH {
        var view = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false)
        return viewVH(view)
    }

    override fun getItemCount(): Int {
        return if (mAlarms == null) 0 else mAlarms!!.size
    }

    override fun onBindViewHolder(holder: viewVH, position: Int) {
        val alarm: Alarm = mAlarms!![position]
        holder.itemView.txtTime.setText(AlarmUtils.getReadableTime(alarm.time))
        holder.itemView.txtLabel.setText(alarm.label)
        holder.itemView.txtSong.setText(
            FileUtils.getFileName(
                context,
                Uri.fromFile(File(alarm.alarM_SONG))
            )
        )
        holder.itemView.setOnClickListener {
            val intent =
                buildAddEditAlarmActivityIntent(holder.itemView.context, EDIT_ALARM, alarm, day)
            holder.itemView.context.startActivity(intent)
        }
        holder.itemView.imgDelete.setOnClickListener {
            delete(alarm, position)
        }
    }

    private fun delete(alarm: Alarm?, position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(
            context,
            R.style.DeleteAlarmDialogTheme
        )
        builder.setTitle(R.string.delete_dialog_title)
        builder.setMessage(R.string.delete_dialog_content)
        builder.setPositiveButton(R.string.yes,
            DialogInterface.OnClickListener { dialogInterface, i -> //Cancel any pending notifications for this alarm
                AlarmReceiver.cancelReminderAlarm(context, alarm!!)
                val rowsDeleted = DatabaseHelper.getInstance(context).deleteAlarm(alarm)
                val messageId: Int
                if (rowsDeleted == 1) {
                    mAlarms!!.removeAt(position)
                    messageId = R.string.delete_complete
                    Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
                    LoadAlarmsService.launchLoadAlarmsService(context, day)
                    notifyDataSetChanged()
                    //   getActivity().finish()
                } else {
                    messageId = R.string.delete_failed
                    Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
                }
            })
        builder.setNegativeButton(R.string.no, null)
        builder.show()
    }

    fun setAlarms(alarms: ArrayList<Alarm>) {
        mAlarms = alarms
        notifyDataSetChanged()
    }
}