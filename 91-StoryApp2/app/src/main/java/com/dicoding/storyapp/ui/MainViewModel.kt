package com.dicoding.storyapp.ui

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.di.Injection
import com.dicoding.storyapp.network.ListStoryItem

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    var token: String? = null

    val story: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStory(token).cachedIn(viewModelScope)

    val storyAsList: LiveData<List<ListStoryItem>> =
        storyRepository.getStoryAsList()
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}