package com.example.epicture

import com.beust.klaxon.Klaxon
import com.example.epicture.services.http.GalleryType
import com.example.epicture.services.http.HomeAlbum
import com.example.epicture.services.http.HomeGallery
import com.example.epicture.services.http.HomeImage
import org.junit.Assert.*
import org.junit.Test
import java.io.StringReader

class ImgurAuthTest {
    @Test
    fun getGallery() {
        val galleryJson = """
        {
            "data": [
                {
                    "id": "12345",
                    "title": "super title",
                    "description": "super description",
                    "datetime": 1000,
                    "cover": "1234",
                    "cover_width": 4096,
                    "cover_height": 4096,
                    "account_url": "Icantstoppooping10",
                    "account_id": 120196735,
                    "privacy": "hidden",
                    "layout": "blog",
                    "views": 38467,
                    "link": "https://imgur.com/a/74TGmEC",
                    "ups": 2339,
                    "downs": 72,
                    "points": 2267,
                    "score": 2286,
                    "is_album": true,
                    "vote": null,
                    "favorite": false,
                    "nsfw": false,
                    "section": "",
                    "comment_count": 160,
                    "favorite_count": 90,
                    "topic": "No Topic",
                    "topic_id": 29,
                    "images_count": 1,
                    "in_gallery": true,
                    "is_ad": false,
                    "tags": [],
                    "ad_type": 0,
                    "ad_url": "",
                    "in_most_viral": true,
                    "include_album_ads": false,
                    "images": [
                        {
                            "id": "OLawVZV",
                            "title": null,
                            "description": "2016 vs now",
                            "datetime": 1603555761,
                            "type": "image/jpeg",
                            "animated": false,
                            "width": 4096,
                            "height": 4096,
                            "size": 752320,
                            "views": 26453,
                            "bandwidth": 19901120960,
                            "vote": null,
                            "favorite": false,
                            "nsfw": null,
                            "section": null,
                            "account_url": null,
                            "account_id": null,
                            "is_ad": false,
                            "in_most_viral": false,
                            "has_sound": false,
                            "tags": [],
                            "ad_type": 0,
                            "ad_url": "",
                            "edited": "0",
                            "in_gallery": false,
                            "link": "https://i.imgur.com/OLawVZV.jpg",
                            "comment_count": null,
                            "favorite_count": null,
                            "ups": null,
                            "downs": null,
                            "points": null,
                            "score": null
                        }
                    ],
                    "ad_config": {
                        "safeFlags": [
                            "album",
                            "sixth_mod_safe",
                            "gallery",
                            "in_gallery"
                        ],
                        "highRiskFlags": [],
                        "unsafeFlags": [],
                        "wallUnsafeFlags": [],
                        "showsAds": true
                    }
                }
            ]
        }
        """.trimIndent()
        val jObj = Klaxon().parseJsonObject(StringReader(galleryJson))
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
        }
        assertEquals("invalid id", homeGallery[0].id, "12345")
        assertEquals("invalid title", homeGallery[0].title, "super title")
        assertEquals("invalid description", homeGallery[0].description, "super description")
        assertEquals("invalid favorite", homeGallery[0].favorite, false)
        assertEquals("invalid is_album", homeGallery[0].is_album, true)
        assertEquals("invalid link", homeGallery[0].link, "https://i.imgur.com/1234.jpeg")
        assertEquals("invalid datetime", homeGallery[0].datetime, 1000)
    }
}