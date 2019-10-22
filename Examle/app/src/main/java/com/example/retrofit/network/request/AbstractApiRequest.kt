package com.example.retrofit.network.request

import android.content.Context
import com.example.retrofit.RetroFitApp
import com.example.retrofit.network.ApiService
import com.example.retrofit.utils.Helper

/**
 * Created by Kim Joonsung on 2019-08-12.
 */
abstract class AbstractApiRequest {

    /**
     * The endpoint for executing the calls.
     */
    protected val apiService: ApiService

    /**
     * Identifies the request.
     */
    protected val tag: String
    protected var context: Context

    protected constructor(apiService: ApiService, tag:String) {
        this.apiService = apiService
        this.tag = tag

        context = RetroFitApp.app!!
    }

    /**
     * Cancels the running request. The response will still be delivered but will be ignored. The
     * implementation should call invalidate on the callback which is used for the request.
     */
    abstract fun cancel()

    /**
     * Check for active internet connection
     *
     * @return boolean
     */
    internal fun isInternetActive(): Boolean {
        return Helper.isInternetActive(context)
    }

    abstract fun execute()
}