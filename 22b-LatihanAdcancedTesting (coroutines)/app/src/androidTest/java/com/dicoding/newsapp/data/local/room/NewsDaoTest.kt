package com.dicoding.newsapp.data.local.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.dicoding.newsapp.DataDummy
import com.dicoding.newsapp.getOrAwaitValue
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*

class NewsDaoTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: NewsDatabase
    private lateinit var dao: NewsDao
    private val sampleNews = DataDummy.generateDummyNewsEntity()[0]

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NewsDatabase::class.java
        ).build()
        dao = database.newsDao()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveNews_Success() = runBlockingTest {
        dao.saveNews(sampleNews)
        val actualNews = dao.getBookmarkedNews().getOrAwaitValue()
        Assert.assertEquals(sampleNews.title, actualNews[0].title)
        Assert.assertTrue(dao.isNewsBookmarked(sampleNews.title).getOrAwaitValue())
    }

    @Test
    fun deleteNews_Success() = runBlockingTest {
        dao.saveNews(sampleNews)
        dao.deleteNews(sampleNews.title)
        val actualNews = dao.getBookmarkedNews().getOrAwaitValue()
        Assert.assertTrue(actualNews.isEmpty())
        Assert.assertFalse(dao.isNewsBookmarked(sampleNews.title).getOrAwaitValue())
    }

}