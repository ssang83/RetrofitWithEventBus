package com.example.retrofit.network

import com.example.retrofit.network.response.AbstractApiResponse
import com.example.retrofit.network.response.ImageListResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Kim Joonsung on 2019-08-12.
 */
interface ApiService {

    @GET("{endPoint}")
    fun getImageList(
        @Path("endPoint") endPoint: String
    ): Call<ImageListResponse>

    /**
     * Upload file to server
     *
     * @param firstName
     * @param lastName
     * @param image0
     * @return
     */
    @Multipart
    @POST("{endPoint}")
    fun uploadFile(
        @Path("endPoint") endPoint: String,
        @Query("first_name") firstName: String,
        @Query("last_name") lastName: String,
        @Part image0: MultipartBody.Part
    ): Call<AbstractApiResponse>
}