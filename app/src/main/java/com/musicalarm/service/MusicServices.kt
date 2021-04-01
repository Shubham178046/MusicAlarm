package com.musicalarm.service

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import androidx.core.app.JobIntentService
import com.musicalarm.helper.hideNotification
import com.musicalarm.model.Alarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MusicServices : JobIntentService(), MediaPlayer.OnCompletionListener {
    //  constructor() : this(TAG)
    //   constructor(name: String) : super(name)

    private var mMediaPlayer: MediaPlayer? = null
    private var alarm: Alarm? = null
    private var uri: Uri? = null
    /* override fun onHandleIntent(intent: Intent?) {
         alarm = intent!!.getParcelableExtra("alarm")
         uri = intent.getParcelableExtra("uri")
         mMediaPlayer = MediaPlayer.create(this, uri)
         mMediaPlayer!!.setOnCompletionListener(this);
         mMediaPlayer!!.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
         val mHandler = Handler()
         mHandler.post(object : Runnable {
             override fun run() {
                 mMediaPlayer?.apply {
                     start()
                 }
             }
         })

     }*/

    companion object {
        private val TAG = MusicServices::class.java.simpleName
        fun launchLoadMusicServices(context: Context, url: Uri, alarm: Alarm) {
            // val launchLoadAlarmsServiceIntent = Intent(context, MusicServices::class.java)
            val launchLoadAlarmsServiceIntent = Intent()
            launchLoadAlarmsServiceIntent.putExtra("uri", url)
            launchLoadAlarmsServiceIntent.putExtra("alarm", alarm)
            enqueueWork(context, MusicServices::class.java, 1, launchLoadAlarmsServiceIntent)
            //   startForegroundService(context, launchLoadAlarmsServiceIntent)
        }
    }

    override fun onCompletion(p0: MediaPlayer?) {
        hideNotification(alarm!!.notificationId())
    }

    override fun onHandleWork(intent: Intent) {
        alarm = intent!!.getParcelableExtra("alarm")
        uri = intent.getParcelableExtra("uri")
        mMediaPlayer = MediaPlayer.create(this, uri)
        mMediaPlayer!!.setOnCompletionListener(this)
        mMediaPlayer!!.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
        Handler(Looper.getMainLooper()).post(object : Runnable {
            override fun run() {
                mMediaPlayer?.apply {
                    start()
                }
            }
        })
    }
}