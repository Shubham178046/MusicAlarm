package com.musicalarm.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.musicalarm.model.Alarm

open class PrefManager {
    constructor(context: Context) {
        sharedPrefManager =
            context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPrefManager.edit()
    }

    companion object {
        lateinit var sharedPrefManager: SharedPreferences
        lateinit var editor: SharedPreferences.Editor
        private var SHARED_PREFERENCE_NAME: String = "session"
        private var ALARM_INFO = "alarm"


        open fun getPrefrence(context: Context): SharedPreferences? {
            return context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        }

        fun setAlarmData(context: Context?, alarm: Alarm?) {
            val gson = Gson()
            val editor: SharedPreferences.Editor = getPrefrence(context!!)!!.edit()
            val json = gson.toJson(alarm)
            editor.putString(ALARM_INFO, json)
            editor.apply()
        }

        fun getAlarmData(context: Context?): Alarm {
            val gson = Gson()
            val json = getPrefrence(context!!)!!.getString(ALARM_INFO, "")
            val type = object : TypeToken<Alarm>() {}.type
            return gson.fromJson(json, type)
        }
    }
}