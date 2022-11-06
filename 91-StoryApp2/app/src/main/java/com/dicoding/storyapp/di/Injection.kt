package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.QuoteRepository
import com.dicoding.storyapp.database.QuoteDatabase
import com.dicoding.storyapp.network.ApiConfig

object Injection {
    fun provideRepository(context: Context): QuoteRepository {
        val database = QuoteDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return QuoteRepository(database, apiService)
    }
}