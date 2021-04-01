package com.musicalarm.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import com.musicalarm.helper.hideNotification
import com.musicalarm.model.Alarm


class MusicService : Service(), MediaPlayer.OnCompletionListener {
    private val ACTION_PLAY: String = "com.musicalarm.action.PLAY"
    private var mMediaPlayer: MediaPlayer? = null
    private var alarm: Alarm? = null
    private var uri: Uri? = null
    private val mBinder: Binder = LocalBinder()
    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action: String = intent.action!!
        alarm = intent.getParcelableExtra("alarm")
        uri = intent.getParcelableExtra("uri")
        mMediaPlayer = MediaPlayer.create(this, uri)
        mMediaPlayer!!.setOnCompletionListener(this);
        mMediaPlayer!!.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
            when (action) {
            ACTION_PLAY -> {
                val mHandler = Handler()
                mHandler.post(object : Runnable {
                    override fun run() {
                        mMediaPlayer?.apply {
                            start()
                        }
                    }
                })
            }
        }
        return START_REDELIVER_INTENT
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        println("TaskRemoved")
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    override fun onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
       return mBinder
    }

    override fun onCompletion(p0: MediaPlayer?) {
        hideNotification(alarm!!.notificationId())
    }
}