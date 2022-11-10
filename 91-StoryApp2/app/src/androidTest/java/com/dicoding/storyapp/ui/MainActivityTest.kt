package com.dicoding.storyapp.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.dicoding.storyapp.R
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.filters.LargeTest
import org.junit.Before
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent


@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @Before
    fun setup(){
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun getStory_Success() {
        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
            onView(withId(R.id.rv_story)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(10))
    }

    @Test
    fun showDetailStory_Success() {
        Intents.init()
        onView(withId(R.id.rv_story)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
            ViewActions.click()
        ))
        intended(hasComponent(StoryDetailActivity::class.java.name))
        onView(withId(R.id.iv_detail_photo)).check(matches(isDisplayed()))
    }

}