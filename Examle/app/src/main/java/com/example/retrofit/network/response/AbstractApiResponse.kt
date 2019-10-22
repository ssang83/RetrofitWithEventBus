package com.example.retrofit.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Kim Joonsung on 2019-08-12.
 */
open class AbstractApiResponse : Serializable {

    // 0 or 1
    /**
     * @return The status
     */
    /**
     * @param status The status
     */
    @SerializedName("status")
    @Expose
    var status: Int = 0

    /**
     * @return The message
     */
    /**
     * @param message The message
     */
    @SerializedName("message")
    @Expose
    var message: String = ""

    /**
     * Identifies the request which was executed to receive this response. The tag is used to make
     * sure that a class which executes a requests only handles the response which is meant for it.
     * This implies that the tag is unique.
     */
    var requestTag: String = ""

}