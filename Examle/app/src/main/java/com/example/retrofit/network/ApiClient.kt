package com.example.retrofit.network

import android.content.Context
import com.example.retrofit.log.Logger
import com.example.retrofit.network.event.RequestFinishedEvent
import com.example.retrofit.network.request.AbstractApiRequest
import com.example.retrofit.network.request.ImageListRequest
import com.example.retrofit.network.request.UploadImageRequest
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Kim Joonsung on 2019-08-12.
 */
class ApiClient(context: Context) {

    companion object {
        private val HTTP_TIMEOUT = 30L // Seconds by default
        private val timeUnit = TimeUnit.SECONDS
        private lateinit var retrofit: Retrofit

        private val WS_SCHEME = "http://"
        private val WS_PREFIX_DOMAIN = "deardhruv.com"
        private val WS_HOSTNAME = "/"
        private val WS_SUFFIX_FOLDER = "api/"

        private var API_BASE_URL = (WS_SCHEME
                + WS_PREFIX_DOMAIN
                + WS_HOSTNAME
                + WS_SUFFIX_FOLDER)

        /**
         * Makes the ApiService calls.
         */
        private lateinit var mApiService: ApiService

        /**
         * The list of running requests. Used to cancel requests.
         */
        private lateinit var requests: MutableMap<String, AbstractApiRequest>

        fun getHttpTimeout() = HTTP_TIMEOUT
    }

    private val context: Context

    init {
        this.context = context
        requests = mutableMapOf()
        EventBus.getDefault().register(this)

        initAPIClient()
    }

    /**
     * A request has finished. Remove it from the list of running requests.
     *
     * @param event The event posted on the EventBus.
     */
    @Subscribe
    fun onEvent(event:RequestFinishedEvent) {
        System.gc()
        requests.remove(event.requestTag)
    }

    // ============================================================================================
    // Request functions
    // ============================================================================================

    /**
     * Execute a request to retrieve the update message. See {@link ApiService#getImageList(String)} for
     * parameter details.
     *
     * @param requestTag The tag for identifying the request.
     */
    fun getImageList(requestTag:String) {
        val request = ImageListRequest(mApiService, requestTag)
        requests.put(requestTag, request)
        request.execute()
    }

    fun uploadImage(requestTag: String, file:String) {
        val request = UploadImageRequest(mApiService, requestTag, file)
        requests.put(requestTag, request)
        request.execute()
    }

    // ============================================================================================
    // Public functions
    // ============================================================================================

    /**
     * Look up the event with the passed tag in the event list. If the request is found, cancel it
     * and remove it from the list.
     *
     * @param requestTag Identifies the request.
     * @return True if the request was cancelled, false otherwise.
     */
    fun cancelRequest(requestTag: String) : Boolean {
        System.gc()
        val request = requests[requestTag]

        if(request != null) {
            request.cancel()
            requests.remove(requestTag)
            return true
        } else {
            return false
        }
    }

    fun isRequestRunning(requestTag: String) = requests.containsKey(requestTag)

    private fun initAPIClient() {
        val okBuilder = MyOkHttpBuilder.getOkHttpBuilder(context)

//        okBuilder.retryOnConnectionFailure(true);
//        okBuilder.followRedirects(false);

        val httpClient = okBuilder.connectTimeout(HTTP_TIMEOUT, timeUnit)
            .writeTimeout(HTTP_TIMEOUT, timeUnit)
            .readTimeout(HTTP_TIMEOUT, timeUnit)
            .build()

        val builder = Retrofit.Builder()
        retrofit = builder
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(API_BASE_URL)
            .client(httpClient)
            .build()

        mApiService = retrofit.create(ApiService::class.java)
    }

    private fun changeApiBaseUrl(newApiBaseUrl:String) {
        API_BASE_URL = newApiBaseUrl
        initAPIClient()
    }
}