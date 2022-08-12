package com.example.librarytest

import android.location.Location
import android.location.LocationManager
import android.os.Build
import java.util.*

data class Position(
    val id: Long = 0,
    val deviceId: String,
    val time: Date,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val speed: Double = 0.0,
    val course: Double = 0.0,
    val accuracy: Double = 0.0,
    val battery: Double = 0.0,
    val mock: Boolean = false,
) {

    constructor(deviceId: String, location: Location, battery: Double) : this(
        deviceId = deviceId,
        time = Date(location.time),
        latitude = location.latitude,
        longitude = location.longitude,
        altitude = location.altitude,
        speed = location.speed * 1.943844, // speed in knots
        course = location.bearing.toDouble(),
        accuracy = if (location.provider != null && location.provider != LocationManager.GPS_PROVIDER) location.accuracy.toDouble() else 0.0,
        battery = battery,
        mock = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) location.isFromMockProvider else false,
    )
}
