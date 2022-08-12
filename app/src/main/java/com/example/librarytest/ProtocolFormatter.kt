package com.example.librarytest

import android.net.Uri

object ProtocolFormatter {

    fun formatRequest(url: String, position: Position, alarm: String? = null): String {
        val serverUrl = Uri.parse(url)
        val builder = serverUrl.buildUpon()
            .appendQueryParameter("id", position.deviceId)
            .appendQueryParameter("timestamp", (position.time.time / 1000).toString())
            .appendQueryParameter("lat", position.latitude.toString())
            .appendQueryParameter("lon", position.longitude.toString())
            .appendQueryParameter("speed", position.speed.toString())
            .appendQueryParameter("bearing", position.course.toString())
            .appendQueryParameter("altitude", position.altitude.toString())
            .appendQueryParameter("accuracy", position.accuracy.toString())
            .appendQueryParameter("batt", position.battery.toString())
        if (position.mock) {
            builder.appendQueryParameter("mock", position.mock.toString())
        }
        if (alarm != null) {
            builder.appendQueryParameter("alarm", alarm)
        }
        return builder.build().toString()
    }
}