package com.example.retrofit.network

import android.content.Context
import com.example.retrofit.log.Logger
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.IOException

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
object MyOkHttpBuilder : HttpLoggingInterceptor.Logger {

    // Cache size for the OkHttpClient
    private val HTTP_DISK_CACHE_SIZE = 30 * 1024 * 1024 // 30 MB

    fun getOkHttpBuilder(context: Context): OkHttpClient.Builder {
        //        Install an HTTP cache in the application cache directory.
        val cacheDir = File(context.cacheDir, "http")
        val cache = Cache(cacheDir, HTTP_DISK_CACHE_SIZE.toLong())

        val builder = OkHttpClient.Builder()
        builder.cache(cache)

        val loggingInterceptor = HttpLoggingInterceptor(this)
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor).addInterceptor(MyInterceptor(context))

        return builder
    }

    override fun log(message: String) {
        if(message.isNotEmpty()) {
            if(!message.contains("Content-Disposition: form-data")) {
                Logger.l(message)
            } else {
                var max = message.length
                if(max > 500) max = 500
                Logger.i(message.substring(0, max))
            }
        }
    }

    private class MyInterceptor(context: Context) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val original = chain.request()

            val requestBuilder = original.newBuilder()
                .header("content-type:", "text/html")
                .header("charset", "UTF-8")
                .method(original.method(), original.body())

            return chain.proceed(requestBuilder.build())
        }
    }
}