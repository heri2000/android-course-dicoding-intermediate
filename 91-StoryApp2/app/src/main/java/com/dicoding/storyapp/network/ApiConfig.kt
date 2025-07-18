package com.dicoding.storyapp.network

import com.dicoding.storyapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
   companion object{
       var BASE_URL = BuildConfig.BASE_API_URL

       fun getApiService(): ApiService {
           val loggingInterceptor =
               HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
           val client = OkHttpClient.Builder()
               .addInterceptor(loggingInterceptor)
               .build()
           val retrofit = Retrofit.Builder()
               .baseUrl(BASE_URL)
               .addConverterFactory(GsonConverterFactory.create())
               .client(client)
               .build()
           return retrofit.create(ApiService::class.java)
       }
   }
}