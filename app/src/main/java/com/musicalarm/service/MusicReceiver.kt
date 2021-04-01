package com.musicalarm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.PowerManager
import android.telephony.ServiceState
import com.musicalarm.helper.hideNotification
import com.musicalarm.helper.startAlarmSound
import com.musicalarm.model.Alarm

class MusicReceiver : BroadcastReceiver(), MediaPlayer.OnCompletionListener {
    private var alarm: Alarm? = null
    private var uri: Uri? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var context: Context? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        this.context = context
        alarm = intent!!.getParcelableExtra("alarm")
        uri = intent.getParcelableExtra("uri")
        val intent = Intent(context, MusicService::class.java)
        intent.action = "com.musicalarm.action.PLAY"
        intent.putExtra("uri", uri)
        intent.putExtra("alarm", alarm)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context!!.startForegroundService(intent)
        } else {
            context!!.startService(intent)
        }
    }

    override fun onCompletion(p0: MediaPlayer?) {
        context!!.hideNotification(alarm!!.notificationId())
    }

}