package com.example.epicture.services.http

import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Personalized HttpCall with okhttp3
 */
object HttpCall {
    public val client: OkHttpClient = OkHttpClient.Builder().build()

    /**
     * Build the request url
     * @param host the url
     * @param pathSegments path segments for the url
     * @param queries string map of string of queries
     */
    fun urlBuilder(
        host: String,
        pathSegments: List<String>?,
        queries: Map<String, String>? = null
    ): HttpUrl {
        val builder = HttpUrl.Builder()
            .scheme("https")
            .host(host)
        if (pathSegments != null) {
            for (pathSegment in pathSegments)
                builder.addPathSegment(pathSegment)
        }
        if (queries != null) {
            for (query in queries) {
                builder.addQueryParameter(query.key, query.value)
            }
        }
        return builder.build()
    }

    /**
     * Build the body request
     * @param arguments string map of string of body arguments
     */
    fun bodyBuilder(arguments: Map<String, String>?): FormBody {
        val builder = FormBody.Builder()
        if (arguments != null) {
            for (argument in arguments) {
                builder.add(argument.key, argument.value)
            }
        }
        return builder.build()
    }

    /**
     * Build a post request
     * @param url the build HttpUrl
     * @param body the build FormBody
     * @param header string map of string arguments for the header
     */
    fun postRequestBuilder(url: HttpUrl, body: FormBody, header: Map<String, String>?): Request {
        val builder = Request.Builder()
            .url(url)
        if (header != null) {
            for (argument in header)
                builder.addHeader(argument.key, argument.value)
        }
        builder.post(body)
        return builder.build()
    }

    /**
     * Build a get request
     * @param url the build HttpUrl
     * @param header string map of string arguments for the header
     */
    fun getRequestBuilder(url: HttpUrl, header: Map<String, String>?): Request {
        val builder = Request.Builder()
            .url(url)
        if (header != null) {
            for (argument in header)
                builder.addHeader(argument.key, argument.value)
        }
        builder.get()
        return builder.build()
    }

    /**
     * Build a delete request
     * @param url the build HttpUrl
     * @param header string map of string arguments for the header
     */
    fun deleteRequestBuilder(url: HttpUrl, header: Map<String, String>?): Request {
        val builder = Request.Builder()
            .url(url)
        if (header != null) {
            for (argument in header)
                builder.addHeader(argument.key, argument.value)
        }
        builder.delete()
        return builder.build()
    }
}