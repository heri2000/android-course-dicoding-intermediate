package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.database.StoryDatabase
import com.dicoding.storyapp.network.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService, context)
    }
}