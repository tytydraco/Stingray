package com.draco.stingray.repositories.local

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

class Permissions(private val context: Context) {
    fun hasSecureSettingsPermissions() =
        context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) ==
        PackageManager.PERMISSION_GRANTED
}