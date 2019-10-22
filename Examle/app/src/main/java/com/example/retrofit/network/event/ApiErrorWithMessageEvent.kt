package com.example.retrofit.network.event

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
class ApiErrorWithMessageEvent(requestTag:String, resultMsgUser:String) {

    val requestTag: String
    val resultMsgUser: String

    init {
        this.requestTag = requestTag
        this.resultMsgUser = resultMsgUser
    }
}