package com.example.gpsdraw.ui.login

import android.content.Context
import android.util.Log


class Preference(context: Context) {
    private val loginPrefName = "LOGIN_PREFERENCE"
    private val loginPreference = context.getSharedPreferences(loginPrefName, Context.MODE_PRIVATE)

    var token: String?
        get() = loginPreference.getString("token", null)
        set(value) {
            Log.d("Preference", "Setting token: $value")
            loginPreference.edit().putString("token", value ?: "").apply()
        }

    var autoLogin: Boolean?
        get() = loginPreference.getBoolean("autoLogin", false)
        set(value) {
            loginPreference.edit().putBoolean("autoLogin", value ?: false).apply()
        }
}
