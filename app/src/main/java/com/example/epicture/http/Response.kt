package com.example.epicture.http

import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import kotlin.reflect.KClass

/**
 * Model class for the auth response
 */
class AuthResponse(
    val access_token: String?,
    val refresh_token: String?,
    val account_id: Int?,
    val expires_in: Int?,
    val account_username: String?
)

/**
 * Model class for the Image Response
 */
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

/**
 * Model class for the AccountBase response
 */
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

/**
 * Model class for Album response
 */
class Album(
    val id: String?,
    val title: String?,
    val description: String?,
    val datetime: Int?,
    val cover: String?,
    val cover_edited: Int?,
    val cover_width: Int?,
    val cover_height: Int?,
    val account_url: String?,
    val account_id: Int?,
    val privacy: String?,
    val layout: String?,
    val views: Int?,
    val link: String?,
    val favorite: Boolean?,
    val images_count: Int?,
    val in_gallery: Boolean?,
    val is_ad: Boolean?,
    val include_album_ads: Boolean?,
    val is_album: Boolean?,
    val deletehash: String?,
    val order: Int?
)

/**
 * Model class for Image of an Album response
 */
class AlbumImage(
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
    val bandwidth: Long?,
    val vote: String?,
    val favorite: Boolean?,
    val account_url: String?,
    val account_id: Int?,
    val is_ad: Boolean?,
    val in_most_viral: Boolean?,
    val has_sound: Boolean?,
    val tags: Array<String?>?,
    val in_gallery: Boolean?,
    val link: String?
)

/**
 * Model class for AccountSettings response
 */
class AccountSettings(
    val account_url: String?,
    val email: String?,
    val avatar: String?,
    val cover: String?,
    val public_images: Boolean?,
    val album_privacy: String?,
    val pro_expiration: Boolean?,
    val accepted_gallery_terms: Boolean?,
    val messaging_enabled: Boolean?,
    val comment_replies: Boolean?,
    val blocked_users: List<String?>?,
    val show_mature: Boolean?,
    val newsletter_subscribed: Boolean?,
    val first_party: Boolean?
)

/**
 * Model class for Gallery response
 */
class Gallery(
    val id: String?,
    val title: String?,
    val description: String?,
    var cover: String?,
    val cover_width: Int?,
    val cover_height: Int?,
    val width: Int?,
    val height: Int?,
    val account_url: String?,
    val account_id: Int?,
    val views: Int?,
    val ups: Int?,
    val downs: Int?,
    val points: Int?,
    val score: Int?,
    val is_album: Boolean?,
    val favorite: Boolean?,
    val favorite_count: Int?,
    val in_gallery: Boolean?,
    val type: String?,
)

/**
 * Model class for an Image of an album in Gallery response
 */
class GalleryAlbumImage(
    val id: String?,
    val title: String?,
    val description: String?,
    val datetime: Int?,
    val type: String?,
    val width: Int?,
    val height: Int?,
    val views: Int?,
    val link: String?
)

/**
 * Model class for the search Gallery response
 */
class SearchGallery(
    val id: String?,
    val title: String?,
    val description: String?,
    val datetime: Int?,
    val cover: String?,
    val cover_width: Int?,
    val cover_height: Int?,
    val account_url: String?,
    val account_id: Int?,
    val privacy: String?,
    val layout: String?,
    val views: Int?,
    val ups: Int?,
    val downs: Int?,
    val points: Int?,
    val score: Int?,
    val is_album: Boolean?,
    val vote: String?,
    val favorite: Boolean?,
    val images_count: Int?,
    val in_gallery: Boolean?,
    val images: List<AlbumImage?>?
)

/**
 * Adapter class to choose the type of the response
 */
class GalleryAdapter : TypeAdapter<GalleryType> {
    override fun classFor(type: Any): KClass<out GalleryType> = when (type as Boolean) {
        true -> HomeAlbum::class
        false -> HomeImage::class
    }
}

/**
 * GalleryType class to choose the correct model class before parsing
 */
@TypeFor(field = "is_album", adapter = GalleryAdapter::class)
open class GalleryType(val is_album: Boolean?)

/**
 * Class model for home album response
 */
data class HomeAlbum(
    val id: String?,
    val title: String?,
    val description: String?,
    val datetime: Int?,
    val cover: String?,
    val cover_width: Int?,
    val cover_height: Int?,
    val account_url: String?,
    val account_id: Int?,
    val privacy: String?,
    val layout: String?,
    val views: Int?,
    val ups: Int?,
    val downs: Int?,
    val points: Int?,
    val score: Int?,
    val vote: String?,
    val favorite: Boolean?,
    val images_count: Int?,
    val in_gallery: Boolean?,
    val images: List<AlbumImage?>?
) : GalleryType(true)

/**
 * Class model for home image response
 */
data class HomeImage(
    val id: String?,
    val title: String?,
    val description: String?,
    val datetime: Int?,
    val link: String?,
    val account_url: String?,
    val account_id: Int?,
    val favorite: Boolean?,
) : GalleryType(false)

/**
 * Class model for home gallery response
 */
class HomeGallery(
    val id: String?,
    val title: String?,
    val description: String?,
    val datetime: Int?,
    val favorite: Boolean?,
    val is_album: Boolean?,
    val link: String?,
)