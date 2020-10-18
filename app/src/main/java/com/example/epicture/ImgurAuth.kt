package com.example.epicture

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.preference.PreferenceManager
import com.beust.klaxon.*
import com.beust.klaxon.Klaxon.*
import com.example.epicture.http.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.Response
import java.io.IOException
import java.io.StringReader

class Config {

    companion object {
        const val clientID : String = "aca4de364cccb3a"
        const val clientSecret : String = "46d9b9dc5c215cf383fa5d786457c76b6194c240"
    }
}

object ImgurAuth {
    private const val clientId: String = Config.clientID
    private const val clientSecret: String = Config.clientSecret
    private const val imgurUrl: String = "api.imgur.com"
    private var connected: Boolean = false
    private var authParams = mutableMapOf<String, String?>(
        "refresh_token" to "",
        "access_token" to "",
        "account_username" to "",
        "account_id" to "",
        "expires_in" to ""
    )

    fun getToken(context: Context) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://$imgurUrl/oauth2/authorize?client_id=$clientId&response_type=token")
        )
        startActivity(context, intent, null)
    }

    fun alreadyConnected(resolve: () -> Unit, reject: () -> Unit) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.context)
        val vals: List<String> =
            listOf("access_token", "refresh_token", "account_username", "account_id", "expires_in")

        Log.d("AUTH", "alreadyConnected...")
        for (param in vals) {
            val preference = prefs.getString(param, "")!!
            if (preference.isEmpty()) {
                Log.d("AUTH", "User not connected")
                authParams.clear()
                return reject()
            }
            authParams[param] = preference
        }
        reloadToken(resolve, reject)
        Log.d("AUTH", "Should skip asking credentials")
    }

    fun savePreferences() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(App.context).edit()
        for (param in authParams) {
            prefs.putString(param.key, param.value)
            prefs.apply()
        }
    }

    fun printCredentials() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.context)
        for (pref in prefs.all) {
            Log.d("AUTH", pref.value.toString())
        }
    }

    private fun reloadToken(resolve: () -> Unit, reject: () -> Unit) {
        val request = HttpCall.postRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("oauth2", "token")),
            HttpCall.bodyBuilder(
                mapOf(
                    "refresh_token" to authParams["refresh_token"].toString(),
                    "client_id" to clientId,
                    "client_secret" to clientSecret,
                    "grant_type" to "refresh_token"
                )
            ),
            mapOf(
                "Authorization" to "Bearer $clientId",
                "Content-Type" to "application/x-www-form-urlencoded"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val res: String = response.body()!!.string()!!
                    val decoded = Klaxon().parse<AuthResponse>(res)
                    authParams["access_token"] = decoded?.access_token
                    authParams["refresh_token"] = decoded?.refresh_token
                    authParams["account_username"] = decoded?.account_username
                    authParams["account_id"] = decoded?.account_id.toString()
                    connected = true
                    return resolve()
                }
                return reject()
            }

        })
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
            connected = true
        }
    }

    fun getImagesByAccountAuth(
        resolve: (List<Image>) -> Unit,
        reject: () -> Unit,
        username: String,
        page: String = "0"
    ) {
        val request = HttpCall.getRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "account", username, "images", page)),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    reject()
                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val jArray = jObj.array<Any>("data")
                val images = Klaxon().parseArray<Image>(jArray?.toJsonString().toString())
                if (images != null) {
                    resolve(images)
                }
                reject()
            }

        })
    }


    fun getImageByIdAuth(resolve: (Image) -> Unit, reject: () -> Unit, username: String, id: String) {
        val request = HttpCall.getRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "account", username, "image", id)),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    reject()
                class ImageById(val data: Image?)
                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val image = Klaxon().parseFromJsonObject<ImageById>(jObj)?.data
                if (image != null) {
                    resolve(image)
                }
                reject()
            }
        })
    }

    fun getAccountBase(resolve: (AccountBase) -> Unit, reject: () -> Unit, username: String) {
        val request = HttpCall.getRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "account", username)),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    reject()
                class Account(val data: AccountBase?)
                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val account = Klaxon().parseFromJsonObject<Account>(jObj)?.data
                if (account != null) {
                    Log.d("AUTH", account.avatar.toString())
                    resolve(account)
                }
                reject()
            }
        })
    }

    fun getAlbumsNoAuth(resolve: (List<Album>) -> Unit, reject: () -> Unit, username: String, page: String = "0") {
        val request = HttpCall.getRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "account", username, "albums", page)),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    reject()
                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val jArray = jObj.array<Any>("data")
                val albums = Klaxon().parseArray<Album>(jArray?.toJsonString().toString())
                if (albums != null) {
                    resolve(albums)
                }
            }

        })
    }

    fun getAlbumImages(resolve: (List<AlbumImage>) -> Unit, reject: () -> Unit, albumId: String) {
        val request = HttpCall.getRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "album", albumId, "images")),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    reject()
                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val jArray = jObj.array<Any>("data")
                val images = Klaxon().parseArray<AlbumImage>(jArray?.toJsonString().toString())
                if (images != null) {
                    resolve(images)
                }
                reject()
            }
        })
    }

    fun getAccountSettings(resolve: (AccountSettings) -> Unit, reject: () -> Unit) {
        val request = HttpCall.getRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "account", "me", "settings")),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    reject()
                class Settings(val data: AccountSettings?)
                val res = response.body()!!.string()!!
                Log.d("AUTH", res)
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val settings = Klaxon().parseFromJsonObject<Settings>(jObj)?.data
                if (settings != null) {
                    resolve(settings)
                }
                reject()
            }
        })
    }

    fun changeAccountSettings(resolve: () -> Unit, reject: () -> Unit, username: String, settings: Map<String, String>) {
        val request = HttpCall.postRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "account", username, "settings")),
            HttpCall.bodyBuilder(settings),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful)
                    resolve()
                reject()
            }

        })
    }
}