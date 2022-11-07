package com.dicoding.storyapp.data

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.network.ApiService
import com.dicoding.storyapp.network.ListStoryItem

class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWxEcUU5WHRVVEM1NzJ0NmIiLCJpYXQiOjE2Njc3MDc2OTZ9.o7YlYzGVbfAEPKIQyDjCU4orv09ZoYbBXX26nZBBQhc"
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStory(token, position, params.loadSize)
            LoadResult.Page(
                data = responseData.listStory as List<ListStoryItem>,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        fun snapshot(dummyStory: List<ListStoryItem>): PagingData<ListStoryItem> {
            TODO("Not yet implemented")
        }

        const val INITIAL_PAGE_INDEX = 1
    }

}