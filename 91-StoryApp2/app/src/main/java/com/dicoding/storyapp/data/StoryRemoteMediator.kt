package com.dicoding.storyapp.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.storyapp.database.StoryDatabase
import com.dicoding.storyapp.database.RemoteKeys
import com.dicoding.storyapp.network.ApiService
import com.dicoding.storyapp.network.ListStoryItem
import com.dicoding.storyapp.preferences.LoginPreferences

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val context: Context
) : RemoteMediator<Int, ListStoryItem>() {

    private lateinit var mLoginPreferences: LoginPreferences
    private var token = ""

    override suspend fun initialize(): InitializeAction {
        mLoginPreferences = LoginPreferences(context)
        val loginInfo = mLoginPreferences.getLogin()
        token = loginInfo.token.toString()

        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getStory("Bearer $token", page, state.config.pageSize)

            val endOfPaginationReached = responseData.listStory?.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached == true) null else page + 1
                val keys = responseData.listStory?.map {
                    it?.id?.let { it1 -> RemoteKeys(id = it1, prevKey = prevKey, nextKey = nextKey) }
                }
                database.remoteKeysDao().insertAll(keys as List<RemoteKeys>)
                database.storyDao().insertStory(responseData.listStory as List<ListStoryItem>)
            }
            return endOfPaginationReached?.let { MediatorResult.Success(endOfPaginationReached = it) }!!
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            data.id.let { database.remoteKeysDao().getRemoteKeysId(it) }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            data.id.let { database.remoteKeysDao().getRemoteKeysId(it) }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ListStoryItem>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}