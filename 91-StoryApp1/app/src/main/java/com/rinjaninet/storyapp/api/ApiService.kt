package com.rinjaninet.storyapp.api

import com.rinjaninet.storyapp.addstory.PostStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("stories/guest")
    fun postStory(
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<PostStoryResponse>

}