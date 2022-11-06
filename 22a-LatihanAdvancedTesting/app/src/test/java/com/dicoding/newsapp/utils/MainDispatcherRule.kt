package com.dicoding.newsapp.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.newsapp.data.NewsRepository
import com.dicoding.newsapp.ui.detail.NewsDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }

    @ExperimentalCoroutinesApi
    @RunWith(MockitoJUnitRunner::class)
    class NewsDetailViewModelTest{

        @get:Rule
        val instantExecutorRule = InstantTaskExecutorRule()

        @Mock
        private lateinit var newsRepository: NewsRepository
        private lateinit var newsDetailViewModel: NewsDetailViewModel
        private val dummyDetailNews = DataDummy.generateDummyNewsEntity()[0]

        @Before
        fun setUp() {
            newsDetailViewModel = NewsDetailViewModel(newsRepository)
            newsDetailViewModel.setNewsData(dummyDetailNews)
        }

        @get:Rule
        val mainDispatcherRule = MainDispatcherRule()

        @Test
        fun `when bookmarkStatus false Should call saveNews`() = runTest {
            val expectedBoolean = MutableLiveData<Boolean>()
            expectedBoolean.value = false
            `when`(newsRepository.isNewsBookmarked(dummyDetailNews.title)).thenReturn(expectedBoolean)
            newsDetailViewModel.bookmarkStatus.value //.getOrAwaitValue()
            newsDetailViewModel.changeBookmark(dummyDetailNews)
            Mockito.verify(newsRepository).saveNews(dummyDetailNews)
        }
    }

}