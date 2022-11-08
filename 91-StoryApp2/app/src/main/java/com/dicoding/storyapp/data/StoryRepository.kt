package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.dicoding.storyapp.database.StoryDatabase
import com.dicoding.storyapp.network.ApiService
import com.dicoding.storyapp.network.ListStoryItem

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                //StoryPagingSource(apiService)
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStoryAsList(): LiveData<List<ListStoryItem>> = storyDatabase.storyDao().getAllStoryAsList()
}