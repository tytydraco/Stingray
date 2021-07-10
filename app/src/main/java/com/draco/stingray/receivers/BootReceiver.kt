package com.draco.stingray.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.draco.stingray.repositories.local.Prefs
import com.draco.stingray.services.ScreenStateService

/**
 * Upon boot completion, start scanning for screen state again
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (Prefs(context).enabled) {
                val screenStateServiceIntent = Intent(context, ScreenStateService::class.java)
                context.startForegroundService(screenStateServiceIntent)
            }
        }
    }
}