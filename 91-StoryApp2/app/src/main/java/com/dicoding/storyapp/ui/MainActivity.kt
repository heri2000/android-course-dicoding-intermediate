package com.dicoding.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.res.ResourcesCompat
import androidx.paging.TerminalSeparatorType
import androidx.paging.insertHeaderItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.LoadingStateAdapter
import com.dicoding.storyapp.adapter.StoryListAdapter
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.network.ListStoryItem
import com.dicoding.storyapp.network.LoginResult
import com.dicoding.storyapp.preferences.ImagePreferences
import com.dicoding.storyapp.preferences.LoginPreferences

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var loginInfo: LoginResult
    private lateinit var mLoginPreferences: LoginPreferences
    private lateinit var mImagePreferences: ImagePreferences
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(this)
    }
    private var newStories = arrayListOf<ListStoryItem>()
    private var loading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddStory.setOnClickListener {
            addStory()
        }

        mImagePreferences = ImagePreferences(this)

        mLoginPreferences = LoginPreferences(this)
        loginInfo = mLoginPreferences.getLogin()

        if (loginInfo.token == null || loginInfo.token!!.isEmpty()) {
            navigateToLogin()
            return
        }

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
            var pagingData = it
            newStories.forEach { story ->
                pagingData = pagingData.insertHeaderItem(TerminalSeparatorType.FULLY_COMPLETE, story)
            }
            adapter.submitData(lifecycle, pagingData)
            if (adapter.itemCount < 1) {
                // Show error message
                binding.ivListStoryErrorIllustration.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.empty_box, null)
                )
                binding.tvListStoryErrorMessage.text = resources.getString(R.string.no_data)
                binding.rvStory.visibility = View.GONE
                binding.groupListStoryErrorMessage.visibility = View.VISIBLE

                if (loading) {
                    loading = false
                    binding.pbListStoryProgressBar.visibility = View.VISIBLE
                } else {
                    binding.pbListStoryProgressBar.visibility = View.GONE
                }
            } else {
                // Hide error message
                binding.rvStory.visibility = View.VISIBLE
                binding.groupListStoryErrorMessage.visibility = View.GONE

                binding.pbListStoryProgressBar.visibility = View.GONE
                loading = false

                saveImageUrls(adapter.snapshot().items as java.util.ArrayList<ListStoryItem>)
            }
        }
    }

    private fun addStory() {
        val addStoryIntent = Intent(this, AddStoryActivity::class.java)
        resultLauncher.launch(
            addStoryIntent,
            ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity)
        )
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AddStoryActivity.RESULT_CODE) {
            val imagePath = result.data?.getStringExtra(AddStoryActivity.EXTRA_IMAGE_PATH)
            val description = result.data?.getStringExtra(AddStoryActivity.EXTRA_DESCRIPTION)
            val latitude = result.data?.getFloatExtra(AddStoryActivity.EXTRA_LATITUDE, -1001F)
            val longitude = result.data?.getFloatExtra(AddStoryActivity.EXTRA_LONGITUDE, -1001F)

            newStories.add(if (latitude != null && longitude != null && latitude > -1000 && longitude > -1000) {
                ListStoryItem(
                    id = "",
                    name = loginInfo.name,
                    photoUrl = imagePath,
                    description = description,
                    lat = latitude.toDouble(),
                    lon = longitude.toDouble()
                )
            } else {
                ListStoryItem(
                    id = "",
                    name = loginInfo.name,
                    photoUrl = imagePath,
                    description = description
                )
            })

            getData()
        }
    }

    private fun saveImageUrls(listStory: ArrayList<ListStoryItem>) {
        val urls = arrayListOf<String>()
        for (item in listStory) {
            urls.add(item.photoUrl.toString())
        }
        mImagePreferences.saveImageUrls(urls)
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