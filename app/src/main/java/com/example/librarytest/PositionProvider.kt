package com.example.librarytest

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.location.Location
import android.os.BatteryManager
import android.util.Log
import androidx.preference.PreferenceManager
import kotlin.math.abs

abstract class PositionProvider(
    protected val context: Context,
    protected val listener: PositionListener,
) {

    interface PositionListener {
        fun onPositionUpdate(position: Position)
        fun onPositionError(error: Throwable)
    }

    protected var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    protected var deviceId = preferences.getString(HelperClass.KEY_DEVICE, "undefined")!!
    protected var interval = preferences.getString(HelperClass.KEY_INTERVAL, "600")!!.toLong() * 1000
    protected var distance: Double = preferences.getString(HelperClass.KEY_DISTANCE, "0")!!.toInt().toDouble()
    protected var angle: Double = preferences.getString(HelperClass.KEY_ANGLE, "0")!!.toInt().toDouble()
    private var lastLocation: Location? = null

    abstract fun startUpdates()
    abstract fun stopUpdates()
    abstract fun requestSingleLocation()

    protected fun processLocation(location: Location?) {
        if (location != null &&
            (lastLocation == null || location.time - lastLocation!!.time >= interval || distance > 0
                    && location.distanceTo(lastLocation) >= distance || angle > 0
                    && abs(location.bearing - lastLocation!!.bearing) >= angle)
        ) {
            Log.i(TAG, "location new")
            lastLocation = location
            listener.onPositionUpdate(Position(deviceId, location, getBatteryLevel(context)))
        } else {
            Log.i(TAG, if (location != null) "location ignored" else "location nil")
        }
    }

    protected fun getBatteryLevel(context: Context): Double {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        if (batteryIntent != null) {
            val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 1)
            return level * 100.0 / scale
        }
        return 0.0
    }

    companion object {
        private val TAG = PositionProvider::class.java.simpleName
        const val MINIMUM_INTERVAL: Long = 1000
    }

}