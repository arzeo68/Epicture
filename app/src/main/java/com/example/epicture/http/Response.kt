package com.example.epicture.http

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.TypeFor

class AuthResponse(
    val access_token: String?,
    val refresh_token: String?,
    val account_id: Int?,
    val expires_in: Int?,
    val account_username: String?
)

class Image(
    val id: String?,
    val title: String?,
    val description: String?,
    val datetime: Int?,
    val type: String?,
    val animated: Boolean?,
    val width: Int?,
    val height: Int?,
    val size: Int?,
    val views: Int?,
    val bandwidth: Int?,
    val vote: String?,
    val favorite: Boolean?,
    val account_url: String?,
    val account_id: Int?,
    val is_ad: Boolean?,
    val in_gallery: Boolean?,
    val deletehash: String?,
    val name: String?,
    val link: String?,
    val tags: Array<String?>?
)

class AccountBase(
    val id: Int?,
    val url: String?,
    val bio: String?,
    val avatar: String?,
    val reputation: Int?,
    val reputation_name: String?,
    val created: Int?,
    val pro_expiration: Boolean?,
)