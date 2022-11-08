package com.dicoding.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.LoadingStateAdapter
import com.dicoding.storyapp.adapter.StoryListAdapter
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.network.LoginResult
import com.dicoding.storyapp.preferences.LoginPreferences

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var loginInfo: LoginResult
    private lateinit var mLoginPreferences: LoginPreferences
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreferences = LoginPreferences(this)
        loginInfo = mLoginPreferences.getLogin()

        if (loginInfo.token == null || loginInfo.token!!.isEmpty()) {
            navigateToLogin()
            return
        }
        Log.d("MainActivity001", loginInfo.toString())
        mainViewModel.token = loginInfo.token

        binding.rvStory.layoutManager = LinearLayoutManager(this)
        getData()
    }

    private fun navigateToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
        finish()
    }

    private fun getData() {
        val adapter = StoryListAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
//            if (adapter.itemCount < 1) {
//                binding.ivListStoryErrorIllustration.setImageDrawable(
//                    ResourcesCompat.getDrawable(resources, R.drawable.empty_box, null)
//                )
//                binding.tvListStoryErrorMessage.text = resources.getString(R.string.no_data)
//                binding.rvStory.visibility = View.GONE
//                binding.groupListStoryErrorMessage.visibility = View.VISIBLE
//            } else {
//                binding.rvStory.visibility = View.VISIBLE
//                binding.groupListStoryErrorMessage.visibility = View.GONE
//            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu.findItem(R.id.action_logout)?.title = resources.getString(R.string.logout, loginInfo.name)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_show_map -> {
                val mapIntent = Intent(this, MapsActivity::class.java)
                startActivity(mapIntent)
            }
            R.id.action_language_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.action_logout -> {
                mLoginPreferences.clearLogin()
                navigateToLogin()
            }
        }
        return true
    }
}