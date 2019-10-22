package com.example.retrofit.network.response

import com.example.retrofit.network.response.submodel.ImageResult
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Kim Joonsung on 2019-08-12.
 */
class ImageListResponse : AbstractApiResponse() {

    @SerializedName("image_list")
    @Expose
    var imageResults:MutableList<ImageResult> = mutableListOf()

}