package com.musicalarm

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.musicalarm.helper.getHideAlarmPendingIntent
import com.musicalarm.helper.getResourceUri
import com.musicalarm.helper.hideNotification
import com.musicalarm.helper.stopAlarmSound
import com.musicalarm.model.Alarm
import com.musicalarm.service.AlarmReceiver
import com.musicalarm.ui.AlarmLandingPageActivity
import com.musicalarm.util.PrefManager
import java.io.File

class Services : Service(), MediaPlayer.OnCompletionListener {
    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false
    private val ACTION_PLAY: String = "com.musicalarm.action.PLAY"
    private var mMediaPlayer: MediaPlayer? = null
    private var alarm: Alarm? = null
    private var uri: Uri? = null
    private val CHANNEL_ID = "alarm_channel"
    var notification: Uri? = null
    var notificationCompat: Notification? = null
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        alarm = PrefManager.getAlarmData(this)
        createNotification()
        startForeground(alarm!!.notificationId(), notificationCompat)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action: String = intent!!.action!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (alarm!!.alarM_SONG != null && !alarm!!.alarM_SONG.equals("") && !alarm!!.alarM_SONG.isNullOrEmpty()) {
                notification =
                    FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        File(alarm!!.alarM_SONG!!)
                    )
            } else {
                notification = getResourceUri(R.raw.ring)
            }
        } else {
            if (alarm!!.alarM_SONG != null && !alarm!!.alarM_SONG.equals("") && !alarm!!.alarM_SONG.isNullOrEmpty()) {
                notification = Uri.fromFile(File(alarm!!.alarM_SONG!!))
            } else {
                notification = getResourceUri(R.raw.ring)
            }
        }
        uri = notification
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
        return START_STICKY
    }

    fun createNotification() {
        val id = alarm!!.notificationId()
        val manager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(this)
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_alarm_vector)
        builder.setColor(ContextCompat.getColor(this, R.color.accent))
        builder.setContentTitle(this.getString(R.string.app_name))
        builder.setContentText(alarm!!.label)
        builder.setTicker(alarm!!.label)
        builder.setVibrate(LongArray(2) { 500 })
        //  builder.setSound(Uri.parse(path))
        builder.setContentIntent(
            launchAlarmLandingPage(
                this,
                alarm!!
            )
        )
        val dismissIntent = this.getHideAlarmPendingIntent(this, alarm!!)
        builder.setAutoCancel(true)
        builder.setPriority(Notification.PRIORITY_HIGH)
        builder.addAction(
            R.drawable.ic_cross_vector,
            this.resources.getString(R.string.dismiss),
            dismissIntent
        )
        builder.setDeleteIntent(dismissIntent)
        notificationCompat = builder.build()
        manager.notify(id, builder.build())
        //  builder.setSound(notification, AudioManager.STREAM_ALARM)
    }

    private fun launchAlarmLandingPage(ctx: Context, alarm: Alarm): PendingIntent? {
        return PendingIntent.getActivity(
            ctx,
            alarm.notificationId(),
            AlarmLandingPageActivity.launchIntent(ctx, true),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createNotificationChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val mgr = ctx.getSystemService(NotificationManager::class.java)
            ?: return
        val name = ctx.getString(R.string.channel_name)
        if (mgr.getNotificationChannel(name) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 500, 1000, 500, 1000, 500)
            channel.setBypassDnd(true)
            mgr.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
        }
    }

    override fun onCompletion(p0: MediaPlayer?) {
        hideNotification(alarm!!.notificationId())
        stopAlarmSound()
    }
}