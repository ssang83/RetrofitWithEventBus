package com.example.retrofit

import android.app.Application
import com.example.retrofit.log.Logger
import com.example.retrofit.network.ApiClient

/**
 * Created by Kim Joonsung on 2019-08-12.
 * Initialization of {@link ApiClient} etc.
 */
class RetroFitApp : Application() {

    private var mApiClient: ApiClient? = null

    companion object {
        lateinit var app: RetroFitApp
            private set
    }

    val apiClient: ApiClient
        get() = if (mApiClient != null) mApiClient!! else initApiClient()

    override fun onCreate() {
        super.onCreate()

        Logger.setLevel(Logger.ALL)
        Logger.setTag("Logger")

        app = this
    }

    /**
     * Initialize the [ApiClient]
     */
    private fun initApiClient(): ApiClient {
        mApiClient = ApiClient(this)
        return mApiClient!!
    }
}