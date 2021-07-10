package com.draco.stingray.services

import android.content.Context
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.draco.stingray.R
import com.draco.stingray.repositories.local.Permissions
import com.draco.stingray.repositories.local.Prefs

class AirplaneTileService : TileService() {
    private lateinit var prefs: Prefs
    private lateinit var permissions: Permissions
    private lateinit var screenStateServiceIntent: Intent

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
        permissions = Permissions(applicationContext)
        screenStateServiceIntent = Intent(applicationContext, ScreenStateService::class.java)
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTileFromPrefs()

        if (prefs.enabled)
            startService(applicationContext)
    }

    private fun startService(context: Context) =
        context.startForegroundService(screenStateServiceIntent)

    private fun stopService(context: Context) =
        context.stopService(screenStateServiceIntent)

    /**
     * Warn the users that we don't have the Secure Settings permission
     */
    private fun warnNoPermission() {
        Toast.makeText(
            applicationContext,
            getString(R.string.toast_permissions),
            Toast.LENGTH_LONG
        )
            .show()
    }

    /**
     * Set the tile status to the saved status
     */
    private fun updateTileFromPrefs() {
        qsTile.state = if (prefs.enabled)
            Tile.STATE_ACTIVE
        else
            Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    /**
     * Flip the tile status
     */
    private fun toggleTile() {
        val newState = if (qsTile.state != Tile.STATE_ACTIVE)
            Tile.STATE_ACTIVE
        else
            Tile.STATE_INACTIVE

        val enabled = newState == Tile.STATE_ACTIVE

        qsTile.state = newState
        qsTile.updateTile()
        prefs.enabled = enabled

        when (enabled) {
            true -> startService(applicationContext)
            false -> stopService(applicationContext)
        }
    }

    override fun onClick() {
        super.onClick()

        if (!permissions.hasSecureSettingsPermissions()) {
            warnNoPermission()
            return
        }

        toggleTile()
    }
}