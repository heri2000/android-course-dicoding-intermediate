package com.rinjaninet.storyapp.api

import com.rinjaninet.storyapp.addstory.AddStoryResponse
import com.rinjaninet.storyapp.login.LoginResponse
import com.rinjaninet.storyapp.register.RegisterResponse
import com.rinjaninet.storyapp.story.GetStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("v1/register")
    fun register(
        @Body registerData: RegisterData
    ): Call<RegisterResponse>

    @POST("v1/login")
    fun login(
        @Body loginData: LoginData
    ): Call<LoginResponse>

    @GET("v1/stories")
    fun getStories(
        @Header("Authorization") token: String
    ): Call<GetStoryResponse>

    @Multipart
    @POST("v1/stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddStoryResponse>

}

data class RegisterData(
    val name: String,
    val email: String,
    val password: String
)

data class LoginData(
    val email: String,
    val password: String
)