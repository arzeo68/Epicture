package com.example.epicture

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity

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
    private var authParams = mutableMapOf<String, String?>("refresh_token" to null, "access_token" to null, "account_username" to null, "account_id" to null)

    fun getToken(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$authUrl?client_id=$clientId&response_type=token"))
        startActivity(context, intent, null)
    }

    fun saveToken(intent: Intent) {
        val uri = intent.data
        Log.d("STATE", intent.data.toString())
        if (uri.toString().startsWith("epicture.dev.com://oauth-intent")) {
            val params = uri.toString().split("#".toRegex()).dropLastWhile {
                it.isEmpty()
            }[1].split("&".toRegex()).dropLastWhile {
                it.isEmpty()
            }
            Log.d("STATE3", params[0])
            for (param in params) {
                val value = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }
                if (authParams.containsKey(value[0])) {
                    authParams[value[0]] = value[1]
                    Log.d("STATE4", value[1])
                }
            }
            connected = true
            Log.d("STATE", authParams["access_token"].toString())
        }
    }
}