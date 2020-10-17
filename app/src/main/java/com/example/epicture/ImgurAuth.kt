package com.example.epicture

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.preference.PreferenceManager

class Config {

    companion object {
        const val clientID : String = "aca4de364cccb3a"
        const val clientSecret : String = "46d9b9dc5c215cf383fa5d786457c76b6194c240"
    }

}

object ImgurAuth {
    private const val clientId: String = Config.clientID
    private const val clientSecret: String = Config.clientSecret
    private const val authUrl: String = "https://api.imgur.com/oauth2/authorize"
    private var connected: Boolean = false
    private var authParams = mutableMapOf<String, String?>("refresh_token" to "", "access_token" to "", "account_username" to "", "account_id" to "")

    fun getToken(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$authUrl?client_id=$clientId&response_type=token"))
        startActivity(context, intent, null)
    }

    fun alreadyConnected(): Boolean {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.context)
        val vals: List<String> = listOf("access_token", "refresh_token", "account_username", "account_id")

        Log.d("AUTH", "alreadyConnected...")
        for (param in vals) {
            val preference = prefs.getString(param, "")!!
            if (preference.isEmpty()) {
                Log.d("AUTH", "User not connected")
                authParams.clear()
                return false
            }
            authParams[param] = preference
        }
        Log.d("AUTH", "Should skip asking credentials")
        return true
    }

    fun printCredentials() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.context)
        for (pref in prefs.all) {
            Log.d("AUTH", pref.value.toString())
        }
    }

    fun reloadToken(): Boolean {
        return false
    }

    fun saveToken(intent: Intent) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(App.context).edit()
        val uri = intent.data
        if (uri.toString().startsWith("epicture.dev.com://oauth-intent")) {
            val params = uri.toString().split("#".toRegex()).dropLastWhile {
                it.isEmpty()
            }[1].split("&".toRegex()).dropLastWhile {
                it.isEmpty()
            }
            for (param in params) {
                val value = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }
                authParams[value[0]] = value[1]
                prefs.putString(value[0], value[1])
                prefs.apply()
            }
            val prefsFinal: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.context)
            connected = true
        }
    }
}