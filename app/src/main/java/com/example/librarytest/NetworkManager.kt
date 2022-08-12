package com.example.librarytest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Log

class NetworkManager(private val context: Context, private val handler: NetworkHandler?) : BroadcastReceiver() {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    interface NetworkHandler {
        fun onNetworkUpdate(isOnline: Boolean)
    }

    val isOnline: Boolean
        get() {
            val activeNetwork = connectivityManager.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }

    fun start() {
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(this, filter)
    }

    fun stop() {
        context.unregisterReceiver(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION && handler != null) {
            val isOnline = isOnline
            Log.i(TAG, "network " + if (isOnline) "on" else "off")
            handler.onNetworkUpdate(isOnline)
        }
    }

    companion object {
        private val TAG = NetworkManager::class.java.simpleName
    }

}