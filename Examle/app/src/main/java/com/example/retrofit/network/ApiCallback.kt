package com.example.retrofit.network

import android.text.TextUtils
import com.example.retrofit.network.event.ApiErrorEvent
import com.example.retrofit.network.event.ApiErrorWithMessageEvent
import com.example.retrofit.network.event.RequestFinishedEvent
import com.example.retrofit.network.response.AbstractApiResponse
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
class ApiCallback<T : AbstractApiResponse> : Callback<T> {

    /**
     * Indicates if the callback was invalidated.
     */
    private var isInvalidated: Boolean = false

    /**
     * The tag of the request which uses this callback.
     */
    private val requestTag: String

    constructor(requestTag:String) {
        isInvalidated = false
        this.requestTag = requestTag
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (isInvalidated || call.isCanceled) {
            finishRequest()
            return
        }
        val result = response.body()
        if (response.isSuccessful && result != null) {
            if (0 == result.status) {
                // Error occurred. Check for error message from api.
                val resultMsgUser = result.message

                if (!TextUtils.isEmpty(resultMsgUser)) {
                    EventBus.getDefault().post(ApiErrorWithMessageEvent(requestTag, resultMsgUser!!))

                } else {
                    EventBus.getDefault().post(ApiErrorEvent(requestTag))
                }
            } else {
                //			modifyResponseBeforeDelivery(result); // Enable when needed.
                result.requestTag = requestTag
                EventBus.getDefault().post(result)
            }
        } else {
            // TODO: Move hardcode string
            EventBus.getDefault().post(
                ApiErrorWithMessageEvent(requestTag, "Server not available.")
            )

            /*

			//TODO: If the Network response code is not between (200..300) and error body is
			//similar to {@link AbstractApiResponse} then use below commented code.
            try {
                AbstractApiResponse abstractApiResponse = (AbstractApiResponse) ApiClient.getRetrofit().responseBodyConverter(
                        AbstractApiResponse.class,
                        AbstractApiResponse.class.getAnnotations())
                        .convert(response.errorBody());
                // Do error handling here

                EventBus.getDefault().post(
                        new ApiErrorWithMessageEvent(requestTag, abstractApiResponse.getMessage()));
//                return;

            } catch (IOException e) {
                e.printStackTrace();
            }
*/
        }
        finishRequest()
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        if (!call.isCanceled && !isInvalidated) {
            EventBus.getDefault().post(ApiErrorEvent(requestTag, t))
        }

        finishRequest()
    }

    /**
     * Invalidates this callback. This means the caller doesn't want to be called back anymore.
     */
    fun invalidate() {
        isInvalidated = true
    }

    /**
     * Posts a [RequestFinishedEvent] on the EventBus to tell the [ApiClient]
     * to remove the request from the list of running requests.
     */
    private fun finishRequest() {
        EventBus.getDefault().post(RequestFinishedEvent(requestTag))
    }

    /**
     * This is for callbacks which extend ApiCallback and want to modify the response before it is
     * delivered to the caller. It is bit different from the interceptors as it allows to implement
     * this method and change the response.
     *
     * @param result The api response.
     */
    protected fun modifyResponseBeforeDelivery(result: T) {
        // Do nothing here. Only for subclasses.
    }

    /**
     * Call this method if No internet connection or other use.
     *
     * @param resultMsgUser User defined messages.
     */
    fun postUnexpectedError(resultMsgUser: String) {
        EventBus.getDefault().post(ApiErrorWithMessageEvent(requestTag, resultMsgUser))
        finishRequest()
    }
}
