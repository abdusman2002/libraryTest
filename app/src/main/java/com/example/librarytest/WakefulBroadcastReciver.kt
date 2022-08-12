package com.example.librarytest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.SparseArray
import androidx.core.content.ContextCompat


abstract class WakefulBroadcastReceiver : BroadcastReceiver() {

    companion object {

        private const val EXTRA_WAKE_LOCK_ID = "android.support.content.wakelockid"
        private val activeWakeLocks = SparseArray<PowerManager.WakeLock>()
        private var nextId = 1

        fun startWakefulForegroundService(context: Context, intent: Intent) {
            synchronized(activeWakeLocks) {
                val id = nextId
                nextId += 1
                if (nextId <= 0) {
                    nextId = 1
                }
                intent.putExtra(EXTRA_WAKE_LOCK_ID, id)
                ContextCompat.startForegroundService(context, intent)
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                val wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    WakefulBroadcastReceiver::class.java.simpleName
                )
                wakeLock.setReferenceCounted(false)
                wakeLock.acquire((60 * 1000).toLong())
                activeWakeLocks.put(id, wakeLock)
            }
        }

        fun completeWakefulIntent(intent: Intent?): Boolean {
            val id = intent?.getIntExtra(EXTRA_WAKE_LOCK_ID, 0) ?: 0
            if (id == 0) {
                return false
            }
            synchronized(activeWakeLocks) {
                val wakeLock = activeWakeLocks[id]
                if (wakeLock != null) {
                    wakeLock.release()
                    activeWakeLocks.remove(id)
                    return true
                }
                return true
            }
        }
    }
}