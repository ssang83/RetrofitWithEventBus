package com.example.retrofit.network.event

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
class RequestFinishedEvent(requestTag:String) {

    /**
     * Identifies the request.
     */
    val requestTag: String

    init {
        this.requestTag = requestTag
    }
}