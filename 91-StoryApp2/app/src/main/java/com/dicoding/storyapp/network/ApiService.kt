package com.dicoding.storyapp.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
   @GET("stories")
   suspend fun getStory(
       @Header("Authorization") token: String,
       @Query("page") page: Int,
       @Query("size") size: Int
   ): StoryResponse   // List<QuoteResponseItem>
}