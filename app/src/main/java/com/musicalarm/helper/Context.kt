package com.musicalarm.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Looper
import android.os.PowerManager
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.widget.Toast
import androidx.annotation.AnyRes
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.musicalarm.model.Alarm
import com.musicalarm.service.HideAlarmService
import com.musicalarm.util.Constants.ALARM_ID
import com.musicalarm.util.Constants.ALARM_SOUND_TYPE_ALARM
import com.musicalarm.util.Constants.ALARM_SOUND_TYPE_NOTIFICATION
import com.musicalarm.util.Constants.DAY_MINUTES
import com.musicalarm.util.Constants.DAY_SECONDS
import com.musicalarm.util.Constants.FRIDAY_BIT
import com.musicalarm.util.Constants.HOUR_SECONDS
import com.musicalarm.util.Constants.MINUTE_SECONDS
import com.musicalarm.util.Constants.MONDAY_BIT
import com.musicalarm.util.Constants.OPEN_ALARMS_TAB_INTENT_ID
import com.musicalarm.util.Constants.PERMISSION_CALL_PHONE
import com.musicalarm.util.Constants.PERMISSION_CAMERA
import com.musicalarm.util.Constants.PERMISSION_GET_ACCOUNTS
import com.musicalarm.util.Constants.PERMISSION_READ_CALENDAR
import com.musicalarm.util.Constants.PERMISSION_READ_CALL_LOG
import com.musicalarm.util.Constants.PERMISSION_READ_CONTACTS
import com.musicalarm.util.Constants.PERMISSION_READ_PHONE_STATE
import com.musicalarm.util.Constants.PERMISSION_READ_SMS
import com.musicalarm.util.Constants.PERMISSION_READ_STORAGE
import com.musicalarm.util.Constants.PERMISSION_RECORD_AUDIO
import com.musicalarm.util.Constants.PERMISSION_SEND_SMS
import com.musicalarm.util.Constants.PERMISSION_WRITE_CALENDAR
import com.musicalarm.util.Constants.PERMISSION_WRITE_CALL_LOG
import com.musicalarm.util.Constants.PERMISSION_WRITE_CONTACTS
import com.musicalarm.util.Constants.PERMISSION_WRITE_STORAGE
import java.io.File
import java.util.*
import kotlin.math.pow

var player: MediaPlayer? = MediaPlayer()


fun Context.getPermissionString(id: Int) = when (id) {
    PERMISSION_READ_STORAGE -> Manifest.permission.READ_EXTERNAL_STORAGE
    PERMISSION_WRITE_STORAGE -> Manifest.permission.WRITE_EXTERNAL_STORAGE
    PERMISSION_CAMERA -> Manifest.permission.CAMERA
    PERMISSION_RECORD_AUDIO -> Manifest.permission.RECORD_AUDIO
    PERMISSION_READ_CONTACTS -> Manifest.permission.READ_CONTACTS
    PERMISSION_WRITE_CONTACTS -> Manifest.permission.WRITE_CONTACTS
    PERMISSION_READ_CALENDAR -> Manifest.permission.READ_CALENDAR
    PERMISSION_WRITE_CALENDAR -> Manifest.permission.WRITE_CALENDAR
    PERMISSION_CALL_PHONE -> Manifest.permission.CALL_PHONE
    PERMISSION_READ_CALL_LOG -> Manifest.permission.READ_CALL_LOG
    PERMISSION_WRITE_CALL_LOG -> Manifest.permission.WRITE_CALL_LOG
    PERMISSION_GET_ACCOUNTS -> Manifest.permission.GET_ACCOUNTS
    PERMISSION_READ_SMS -> Manifest.permission.READ_SMS
    PERMISSION_SEND_SMS -> Manifest.permission.SEND_SMS
    PERMISSION_READ_PHONE_STATE -> Manifest.permission.READ_PHONE_STATE
    else -> ""
}

fun Context.hasPermission(permId: Int) = ContextCompat.checkSelfPermission(
    this, getPermissionString(
        permId
    )
) == PackageManager.PERMISSION_GRANTED

fun Context.getDefaultAlarmUri(type: Int) =
    RingtoneManager.getDefaultUri(if (type == ALARM_SOUND_TYPE_NOTIFICATION) RingtoneManager.TYPE_NOTIFICATION else RingtoneManager.TYPE_ALARM)

fun Context.startAlarmSound(context: Context, url: Uri) {
    player = MediaPlayer.create(context, url)
    player!!.isLooping = true
    player!!.start()
}

fun Context.stopAlarmSound() {
    if (player != null) {
        player!!.stop()
    }
}
internal fun Context.getResourceUri(@AnyRes resourceId: Int): Uri = Uri.Builder()
    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
    .authority(packageName)
    .path(resourceId.toString())
    .build()


fun getTomorrowBit(): Int {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_WEEK, 1)
    val dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
    return 2.0.pow(dayOfWeek).toInt()
}

fun Context.grantReadUriPermission(uriString: String) {
    try {
        // ensure custom reminder sounds play well
        grantUriPermission(
            "com.android.systemui",
            Uri.parse(uriString),
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    } catch (ignored: Exception) {
    }
}


fun Context.hideNotification(id: Int) {
    val manager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.cancel(id)
}
fun Context.getHideAlarmPendingIntent(context: Context,alarm: Alarm): PendingIntent {
    val intent = Intent(this, HideAlarmService::class.java)
    intent.putExtra(ALARM_ID, alarm.notificationId())
    return PendingIntent.getBroadcast(this, alarm.notificationId(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun getCurrentDayMinutes(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
}

fun  Context.getfilename(context: Context,pat :String) :String
{
    var SongTitle: String ?=""
    var contentResolver = context.getContentResolver()
    var cursor = contentResolver.query(
        Uri.fromFile(File(pat)),  // Uri
        null,
        null,
        null,
        null
    )
    if (cursor == null) {
        Toast.makeText(context, "Something Went Wrong.", Toast.LENGTH_LONG)
    } else if (!cursor.moveToFirst()) {
        Toast.makeText(context, "No Music Found on SD Card.", Toast.LENGTH_LONG)
    } else {
        val Title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
        do {
             SongTitle = cursor.getString(Title)

        } while (cursor.moveToNext())
    }
    return SongTitle!!
}
