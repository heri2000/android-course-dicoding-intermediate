package com.rinjaninet.storyapp.story

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rinjaninet.storyapp.R
import com.rinjaninet.storyapp.api.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListStoryViewModel : ViewModel() {

    private val _listStory = MutableLiveData<ArrayList<ListStoryItem>?>()
    val listStory: LiveData<ArrayList<ListStoryItem>?> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        _isLoading.value = false
        _errorMessage.value = null
    }

    fun getStories(token: String, resources: Resources) {
        _isLoading.value = true
        val service = ApiConfig().getApiService().getStories("Bearer $token")
        service.enqueue(object : Callback<GetStoryResponse> {
            override fun onResponse(
                call: Call<GetStoryResponse>,
                response: Response<GetStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.error != null && !responseBody.error) {
                        _errorMessage.value = null
                        _isLoading.value = false
                        _listStory.value = responseBody.listStory
                    } else {
                        _errorMessage.value = responseBody?.message
                        _isLoading.value = false
                    }
                } else {
                    _errorMessage.value = resources.getString(R.string.error_list_story_0301)
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<GetStoryResponse>, t: Throwable) {
                _errorMessage.value = resources.getString(R.string.error_list_story_0302)
                _isLoading.value = false
            }

        })
    }

}