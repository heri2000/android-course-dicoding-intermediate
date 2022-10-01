package com.rinjaninet.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.rinjaninet.storyapp.addstory.AddStoryActivity
import com.rinjaninet.storyapp.databinding.ActivityMainBinding
import com.rinjaninet.storyapp.login.LoginActivity
import com.rinjaninet.storyapp.login.LoginResult
import com.rinjaninet.storyapp.preferences.LoginPreferences
import com.rinjaninet.storyapp.story.ListStoryAdapter
import com.rinjaninet.storyapp.story.ListStoryViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var loginInfo: LoginResult
    private lateinit var mLoginPreferences: LoginPreferences
    private val listStoryViewModel by viewModels<ListStoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreferences = LoginPreferences(this)
        loginInfo = mLoginPreferences.getLogin()

        if (loginInfo.token == null || loginInfo.token!!.isEmpty()) navigateToLogin()

        binding.btnAddStory.setOnClickListener {
            val addStoryIntent = Intent(this, AddStoryActivity::class.java)
            startActivity(addStoryIntent)
        }

        displayProgress()
        displayErrorMessage()
        displayListStory()
    }

    private fun navigateToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
        finish()
    }


    private fun displayProgress() {
        listStoryViewModel.isLoading.observe(this) { isLoading ->
            binding.pbListStoryProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun displayErrorMessage() {
        listStoryViewModel.errorMessage.observe(this) { errorMessage ->
            binding.apply {
                if (errorMessage == null) {
                    rvListStory.visibility = View.VISIBLE
                    groupListStoryErrorMessage.visibility = View.GONE
                    tvListStoryErrorMessage.text = ""
                } else {
                    rvListStory.visibility = View.GONE
                    groupListStoryErrorMessage.visibility = View.VISIBLE
                    tvListStoryErrorMessage.text = errorMessage
                }
            }
        }

        listStoryViewModel.errorType.observe(this) { errorType ->
            binding.apply {
                when (errorType) {
                    ListStoryViewModel.ERROR_TYPE_NONE ->
                        ivListStoryErrorIllustration.setImageDrawable(null)
                    ListStoryViewModel.ERROR_TYPE_NO_DATA ->
                        ivListStoryErrorIllustration.setImageDrawable(
                            ResourcesCompat.getDrawable(resources, R.drawable.empty_box, null)
                        )
                    ListStoryViewModel.ERROR_TYPE_CONNECTION ->
                        ivListStoryErrorIllustration.setImageDrawable(
                            ResourcesCompat.getDrawable(resources, R.drawable.unplugged, null)
                        )
                    ListStoryViewModel.ERROR_TYPE_UNKNOWN ->
                        ivListStoryErrorIllustration.setImageDrawable(
                            ResourcesCompat.getDrawable(resources, R.drawable.question_mark, null)
                        )
                }
            }
        }
    }
    private fun displayListStory() {
        listStoryViewModel.listStory.observe(this) { listStory ->
            binding.apply {
                rvListStory.setHasFixedSize(true)
                val layoutManager = LinearLayoutManager(this@MainActivity)
                rvListStory.layoutManager = layoutManager
                if (listStory?.size == 0) {
                    rvListStory.visibility = View.INVISIBLE
                    groupListStoryErrorMessage.visibility = View.VISIBLE
                } else {
                    rvListStory.visibility = View.VISIBLE
                    groupListStoryErrorMessage.visibility = View.GONE
                }
                Log.d("AAAAA", listStory.toString())
                val listStoryAdapter = listStory?.let { ListStoryAdapter(it) }
                rvListStory.adapter = listStoryAdapter
            }
        }

        if (loginInfo.token != null)
            listStoryViewModel.getStories(loginInfo.token ?: "", resources)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu?.findItem(R.id.action_logout)?.title = resources.getString(
            R.string.logout, loginInfo.name
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                mLoginPreferences.clearLogin()
                navigateToLogin()
            }
        }
        return true
    }
}