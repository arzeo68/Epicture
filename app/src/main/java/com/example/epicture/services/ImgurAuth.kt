package com.example.epicture

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.preference.PreferenceManager
import com.beust.klaxon.Klaxon
import com.example.epicture.config.Config
import com.example.epicture.services.http.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.io.StringReader

/**
 * The ImgurAuth object deal with the Imgur API
 * It manage all the available call to the API
 */
public object ImgurAuth {
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

    /**
     * Set the user name of the actual user
     * @param username the new username
     */
    fun setUsername(username: String) {
        authParams["account_username"] = username
    }

    /**
     * Open a navigator with the login page of Imgur to get user credentials
     * @param context Base context for the login activity
     */
    fun getToken(context: Context) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://$imgurUrl/oauth2/authorize?client_id=$clientId&response_type=token")
        )
        startActivity(context, intent, null)
    }

    /**
     * Check if the user is connected, reload the actual credentials and call the resolve function
     * Otherwise call the reject function
     * @param resolve resolve callback
     * @param reject reject callback
     */
    fun alreadyConnected(resolve: () -> Unit, reject: () -> Unit) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.context)
        val vals: List<String> =
            listOf("access_token", "refresh_token", "account_username", "account_id", "expires_in")

        for (param in vals) {
            val preference = prefs.getString(param, "")!!
            if (preference.isEmpty()) {
                authParams.clear()
                return reject()
            }
            authParams[param] = preference
        }
        reloadToken(resolve, reject)
    }

    /**
     * Logout the user into removing his credentials
     */
    fun logout() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(App.context).edit()
        prefs.clear()
        prefs.apply()
    }

    /**
     * Save the credentials preferences
     */
    fun savePreferences() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(App.context).edit()
        for (param in authParams) {
            prefs.putString(param.key, param.value)
            prefs.apply()
        }
    }

    /**
     * Print the user preferences
     */
    fun printCredentials() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.context)
        for (pref in prefs.all) {
        }
    }

    /**
     * Reload the user credentials
     * @param resolve resolve callback
     * @param reject reject callback
     */
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

    /**
     * Save the credentials when the navigator activity is over
     * @param intent the activity intent response
     */
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

    /**
     * Get Images of the actual user connected on Imgur
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param username the username
     * @param page the pagination id
     */
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
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    return reject()
                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val jArray = jObj.array<Any>("data")
                val images = Klaxon().parseArray<Image>(jArray?.toJsonString().toString())
                if (images != null) {
                    return resolve(images)
                }
                return reject()
            }

        })
    }

    /**
     * Get an Image by ID of the connected user
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param username the username
     * @param id the image ID
     */
    fun getImageByIdAuth(
        resolve: (Image) -> Unit,
        reject: () -> Unit,
        username: String,
        id: String
    ) {
        val request = HttpCall.getRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "account", username, "image", id)),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    return reject()
                    return
                }
                class ImageById(val data: Image?)

                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val image = Klaxon().parseFromJsonObject<ImageById>(jObj)?.data
                if (image != null) {
                    return resolve(image)
                }
                return reject()
            }
        })
    }

    /**
     * Get Account Base of the connected user on Imgur
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param username the username
     */
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
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    return reject()
                }
                class Account(val data: AccountBase?)

                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val account = Klaxon().parseFromJsonObject<Account>(jObj)?.data
                if (account != null) {
                    return resolve(account)
                }
                return reject()
            }
        })
    }

    /**
     * Get Images of an user on Imgur (no auth)
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param username the username
     * @param page the pagination id
     */
    fun getAlbumsNoAuth(
        resolve: (List<Album>) -> Unit,
        reject: () -> Unit,
        username: String,
        page: String = "0"
    ) {
        val request = HttpCall.getRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "account", username, "albums", page)),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    return reject()
                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val jArray = jObj.array<Any>("data")
                val albums = Klaxon().parseArray<Album>(jArray?.toJsonString().toString())
                if (albums != null) {
                    return resolve(albums)
                }
                return reject()
            }

        })
    }

    /**
     * Get Images of an album on Imgur
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param albumId the album ID
     */
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
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    return reject()
                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val jArray = jObj.array<Any>("data")
                val images = Klaxon().parseArray<AlbumImage>(jArray?.toJsonString().toString())
                if (images != null) {
                    return resolve(images)
                }
                return reject()
            }
        })
    }

    /**
     * Get Account Settings of the actual connected user on Imgur
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     */
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
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    return reject()
                class Settings(val data: AccountSettings?)

                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val settings = Klaxon().parseFromJsonObject<Settings>(jObj)?.data
                if (settings != null) {
                    return resolve(settings)
                }
                return reject()
            }
        })
    }

    /**
     * Change Account Settings of the actual user connected on Imgur
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param username the username
     * @param settings a string map of string which contain the parameters you want to change
     */
    fun changeAccountSettings(
        resolve: () -> Unit,
        reject: () -> Unit,
        username: String,
        settings: Map<String, String>
    ) {
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
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    return resolve()
                }
                return reject()
            }

        })
    }

    /**
     * Get Gallery on Imgur
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param page the pagination id
     * @param section the section type
     * @param sort the sort type
     * @param window the window type
     */
    fun getGallery(
        resolve: (ArrayList<HomeGallery>) -> Unit,
        reject: () -> Unit,
        page: String = "0",
        section: String = "hot",
        sort: String = "viral",
        window: String = "day"
    ) {
        val request = HttpCall.getRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "gallery", section, sort, window, page)),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val jArray = jObj.array<Any>("data")
                val galleryList =
                    Klaxon().parseArray<GalleryType>(jArray?.toJsonString().toString())
                val homeGallery: ArrayList<HomeGallery> = ArrayList()
                if (galleryList != null) {
                    for (gallery in galleryList) {
                        if (gallery.is_album == true) {
                            val h = gallery as HomeAlbum
                            homeGallery.add(
                                HomeGallery(
                                    h.id,
                                    h.title,
                                    h.description,
                                    h.datetime,
                                    h.favorite,
                                    h.is_album,
                                    "https://i.imgur.com/${gallery.cover}.${
                                        gallery.images?.get(0)?.type?.substring(
                                            6
                                        )
                                    }"
                                )
                            )
                        } else {
                            val h = gallery as HomeImage
                            homeGallery.add(
                                HomeGallery(
                                    h.id,
                                    h.title,
                                    h.description,
                                    h.datetime,
                                    h.favorite,
                                    h.is_album,
                                    h.link
                                )
                            )
                        }
                    }
                    return resolve(homeGallery)
                }
                return reject()
            }

        })
    }

    /**
     * Get the Favorite Images or Albums of the actual user connected on Imgur
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param page the pagination id
     */
    fun getFavorites(resolve: (List<Gallery>) -> Unit, reject: () -> Unit, page: String = "") {
        val request = HttpCall.getRequestBuilder(
            HttpCall.urlBuilder(
                imgurUrl,
                listOf("3", "account", authParams["account_username"].toString(), "favorites", page)
            ),
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
                    return reject()
                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val jArray = jObj.array<Any>("data")
                val galleries = Klaxon().parseArray<Gallery>(jArray?.toJsonString().toString())
                if (galleries != null) {
                    for (gallery in galleries) {
                        val type = gallery.type.toString().substring(6)
                        gallery.cover = "https://i.imgur.com/${gallery.cover}.${type}"
                    }
                    return resolve(galleries)
                }
                return reject()
            }
        })
    }

    /**
     * Get Images of a Gallery on Imgur
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param galleryId the gallery ID
     */
    fun getGalleryImages(
        resolve: (List<GalleryAlbumImage>) -> Unit,
        reject: () -> Unit,
        galleryId: String
    ) {
        val request = HttpCall.getRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "gallery", "album", galleryId)),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    return reject()
                class AlbumImages(val images: List<GalleryAlbumImage>)
                class AlbumData(val data: AlbumImages)

                val res = response.body()!!.string()!!
                val images = Klaxon().parse<AlbumData>(res)
                if (images != null) {
                    return resolve(images.data.images)
                }
                return reject()
            }
        })
    }

    /**
     * Put in favorite an Album or Image on Imgur
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param id the album/image ID
     * @param type the type of the post to put in favorite (image or album)
     */
    fun putFavorite(resolve: () -> Unit, reject: () -> Unit, id: String, type: String = "image") {
        val request = HttpCall.postRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", type, id, "favorite")),
            HttpCall.bodyBuilder(null),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    return reject()
                return resolve()
            }
        })
    }

    /**
     * Search Album and Images on Imgur
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param q the tags you want to search
     * @param page the pagination id
     * @param sort the sort type
     * @param window the window type
     */
    fun searchGallery(
        resolve: (ArrayList<HomeGallery>) -> Unit,
        reject: () -> Unit,
        q: String,
        page: String = "0",
        sort: String = "time",
        window: String = "all"
    ) {
        val request = HttpCall.getRequestBuilder(
            HttpCall.urlBuilder(
                imgurUrl,
                listOf("3", "gallery", "search", sort, window, page),
                mapOf("q" to q)
            ),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body()!!.string()!!
                val jObj = Klaxon().parseJsonObject(StringReader(res))
                val jArray = jObj.array<Any>("data")
                val galleryList =
                    Klaxon().parseArray<GalleryType>(jArray?.toJsonString().toString())
                val homeGallery: ArrayList<HomeGallery> = ArrayList()
                if (galleryList != null) {
                    for (gallery in galleryList) {
                        if (gallery.is_album == true) {
                            val h = gallery as HomeAlbum
                            homeGallery.add(
                                HomeGallery(
                                    h.id,
                                    h.title,
                                    h.description,
                                    h.datetime,
                                    h.favorite,
                                    h.is_album,
                                    "https://i.imgur.com/${gallery.cover}.${
                                        gallery.images?.get(0)?.type?.substring(
                                            6
                                        )
                                    }"
                                )
                            )
                        } else {
                            val h = gallery as HomeImage
                            homeGallery.add(
                                HomeGallery(
                                    h.id,
                                    h.title,
                                    h.description,
                                    h.datetime,
                                    h.favorite,
                                    h.is_album,
                                    h.link
                                )
                            )
                        }
                    }
                    return resolve(homeGallery)
                }
                return reject()
            }
        })
    }

    /**
     * Delete an Image or Album of the actual user connected on Imgur
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param type the type of the post which need to be deleted
     * @param username the username of the user
     * @param id the ID of the image/album
     */
    fun deleteImageOrAlbum(
        resolve: () -> Unit,
        reject: () -> Unit,
        type: String,
        username: String,
        id: String
    ) {
        val request = HttpCall.deleteRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "account", username, type, id)),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    return reject()
                }
                return resolve()
            }
        })
    }

    /**
     * Upload an image of the actual user connected on Imgur
     * @param resolve resolve callback which take a list of Response Image as parameter
     * @param reject reject callback
     * @param type the type of the post (image or video)
     * @param urlType the url type of the post (base64 or URL)
     * @param name the name of the post
     * @param title the title of the post
     * @param description the description of the post
     */
    fun uploadImage(
        resolve: () -> Unit,
        reject: () -> Unit,
        type: String,
        base64: String,
        urlType: String,
        name: String,
        title: String,
        description: String
    ) {
        val request = HttpCall.postRequestBuilder(
            HttpCall.urlBuilder(imgurUrl, listOf("3", "upload")),
            HttpCall.bodyBuilder(
                mapOf(
                    type to base64,
                    "type" to urlType,
                    "name" to name,
                    "title" to title,
                    "description" to description
                )
            ),
            mapOf(
                "Authorization" to "Client-ID $clientId",
                "Authorization" to "Bearer ${authParams["access_token"]}"
            )
        )
        HttpCall.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                return reject()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    return reject()
                return resolve()
            }
        })
    }
}