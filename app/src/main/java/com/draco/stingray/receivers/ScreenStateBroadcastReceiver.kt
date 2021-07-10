package com.draco.stingray.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenStateBroadcastReceiver : BroadcastReceiver() {
    /**
     * Report when the screen state changes; register this in a service
     */
    var screenStateListener: ((Boolean) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> screenStateListener?.invoke(true)
            Intent.ACTION_SCREEN_OFF -> screenStateListener?.invoke(false)
        }
    }
}