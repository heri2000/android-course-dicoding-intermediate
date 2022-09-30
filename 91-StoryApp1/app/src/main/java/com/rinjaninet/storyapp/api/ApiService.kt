package com.rinjaninet.storyapp.api

import com.rinjaninet.storyapp.addstory.PostStoryResponse
import com.rinjaninet.storyapp.register.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("v1/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @Multipart
    @POST("v1/stories/guest")
    fun postStory(
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<PostStoryResponse>

}