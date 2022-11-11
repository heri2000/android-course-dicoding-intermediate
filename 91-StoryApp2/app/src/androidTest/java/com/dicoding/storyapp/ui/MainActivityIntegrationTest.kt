package com.dicoding.storyapp.ui

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import com.dicoding.storyapp.R
import androidx.test.espresso.assertion.ViewAssertions.matches
import org.junit.Before
import androidx.test.filters.MediumTest
import com.dicoding.storyapp.network.ApiConfig
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import java.io.IOException
import java.io.InputStreamReader


@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@MediumTest
class MainActivityIntegrationTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getStory_Success() {
        ActivityScenario.launch(MainActivity::class.java)

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
        onView(withText("cek")).check(matches(isDisplayed()))
    }

}

object JsonConverter {
    fun readStringFromFile(fileName: String): String {
        try {
            val applicationContext = ApplicationProvider.getApplicationContext<Context>()
            val inputStream = applicationContext.assets.open(fileName)
            val builder = StringBuilder()
            val reader = InputStreamReader(inputStream, "UTF-8")
            reader.readLines().forEach {
                builder.append(it)
            }
            return builder.toString()
        } catch (e: IOException) {
            throw e
        }
    }
}