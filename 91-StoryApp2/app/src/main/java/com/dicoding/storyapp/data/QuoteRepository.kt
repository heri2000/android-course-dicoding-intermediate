package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.dicoding.storyapp.database.QuoteDatabase
import com.dicoding.storyapp.network.ApiService
import com.dicoding.storyapp.network.QuoteResponseItem

class QuoteRepository(private val quoteDatabase: QuoteDatabase, private val apiService: ApiService) {
    fun getQuote(): LiveData<PagingData<QuoteResponseItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = QuoteRemoteMediator(quoteDatabase, apiService),
            pagingSourceFactory = {
                //QuotePagingSource(apiService)
                quoteDatabase.quoteDao().getAllQuote()
            }
        ).liveData
    }
}