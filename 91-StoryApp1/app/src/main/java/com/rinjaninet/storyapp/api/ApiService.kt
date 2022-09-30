package com.rinjaninet.storyapp.api

import com.rinjaninet.storyapp.addstory.PostStoryResponse
import com.rinjaninet.storyapp.login.LoginResponse
import com.rinjaninet.storyapp.register.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //@POST("v1/register")
    //fun register(
    //    @Field("name") name: String,
    //    @Field("email") email: String,
    //    @Field("password") password: String
    //): Call<RegisterResponse>

    //@POST("v1/login")
    //fun login(
    //    @Field("email") email: String,
    //    @Field("password") password: String
    //): Call<LoginResponse>

    @POST("v1/register")
    fun register(
        @Body registerData: RegisterData
    ): Call<RegisterResponse>

    @POST("v1/login")
    fun login(
        @Body loginData: LoginData
    ): Call<LoginResponse>

    @Multipart
    @POST("v1/stories/guest")
    fun postStory(
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<PostStoryResponse>

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