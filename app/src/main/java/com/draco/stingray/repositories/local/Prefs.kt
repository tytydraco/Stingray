package com.draco.stingray.repositories.local

import android.content.Context
import com.draco.stingray.R

class Prefs(private val context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(
            context.getString(R.string.pref_file_key),
            Context.MODE_PRIVATE
        )

    var enabled: Boolean
        get() = sharedPreferences
            .getBoolean(context.getString(R.string.pref_enabled), false)
        set(value) = sharedPreferences
            .edit()
            .putBoolean(context.getString(R.string.pref_enabled), value)
            .apply()
}