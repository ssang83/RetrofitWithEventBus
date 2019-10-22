package com.example.retrofit.network.request

import com.example.retrofit.R
import com.example.retrofit.network.ApiCallback
import com.example.retrofit.network.ApiService
import com.example.retrofit.network.response.ImageListResponse
import com.example.retrofit.utils.Helper.isInternetActive
import retrofit2.Call

/**
 * Created by Kim Joonsung on 2019-08-13.
 * See super constructor {@link AbstractApiRequest#AbstractApiRequest(ApiService, String)}.
 */
class ImageListRequest(apiService: ApiService, tag: String) : AbstractApiRequest(apiService, tag) {

    /**
     * The callback used for this request. Declared globally for cancellation. See [ ][.cancel].
     */
    private lateinit var callback: ApiCallback<ImageListResponse>

    /**
     * To cancel REST API call from Retrofit. See [.cancel].
     */
    private lateinit var call: Call<ImageListResponse>

    override fun execute() {
        callback = ApiCallback(tag)

        if(!isInternetActive()) {
            callback.postUnexpectedError(context.getString(R.string.error_no_internet))
            return
        }

        call = apiService.getImageList(context.getResources().getString(R.string.api_image_list))
        call.enqueue(callback)
    }

    override fun cancel() {
        callback.invalidate()
        call.cancel()
    }
}