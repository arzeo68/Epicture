package com.example.epicture.http

import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

object HttpCall {
    public val client: OkHttpClient = OkHttpClient.Builder().build()

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

    fun bodyBuilder(arguments: Map<String, String>?): FormBody {
        val builder = FormBody.Builder()
        if (arguments != null) {
            for (argument in arguments) {
                builder.add(argument.key, argument.value)
            }
        }
        return builder.build()
    }

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