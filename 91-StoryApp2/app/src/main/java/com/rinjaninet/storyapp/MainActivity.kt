package com.rinjaninet.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.rinjaninet.storyapp.addstory.AddStoryActivity
import com.rinjaninet.storyapp.databinding.ActivityMainBinding
import com.rinjaninet.storyapp.login.LoginActivity
import com.rinjaninet.storyapp.network.LoginResult
import com.rinjaninet.storyapp.preferences.ImagePreferences
import com.rinjaninet.storyapp.preferences.LoginPreferences
import com.rinjaninet.storyapp.story.ListStoryAdapter
import com.rinjaninet.storyapp.network.ListStoryItem
import com.rinjaninet.storyapp.story.ListStoryViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var loginInfo: LoginResult
    private lateinit var mLoginPreferences: LoginPreferences
    private lateinit var mImagePreferences: ImagePreferences
    private val listStoryViewModel by viewModels<ListStoryViewModel>()
    private var listStory: ArrayList<ListStoryItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreferences = LoginPreferences(this)
        loginInfo = mLoginPreferences.getLogin()
        loginInfo.token?.let { Log.d("aaaaa", it) }

        mImagePreferences = ImagePreferences(this)

        if (loginInfo.token == null || loginInfo.token!!.isEmpty()) navigateToLogin()

        binding.btnAddStory.setOnClickListener {
            addStory()
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
                    groupListStoryErrorMessage.visibility = View.GONE
                } else {
                    tvListStoryErrorMessage.text = errorMessage
                    groupListStoryErrorMessage.visibility = View.VISIBLE
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
        binding.apply {
            rvListStory.setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(this@MainActivity)
            rvListStory.layoutManager = layoutManager

            listStoryViewModel.listStory.observe(this@MainActivity) { listStory ->
                if (listStory?.size == 0) {
                    rvListStory.visibility = View.INVISIBLE
                } else {
                    rvListStory.visibility = View.VISIBLE
                }
                if (listStory != null) {
                    val listStoryAdapter = ListStoryAdapter(listStory)
                    rvListStory.adapter = listStoryAdapter
                    this@MainActivity.listStory = listStory

                    saveImageUrls(listStory)
                }
            }
        }

        if (loginInfo.token != null)
            listStoryViewModel.getStories(loginInfo.token ?: "", resources)
    }

    private fun saveImageUrls(listStory: ArrayList<ListStoryItem>) {
        val urls = arrayListOf<String>()
        for (item in listStory) {
            urls.add(item.photoUrl.toString())
        }
        mImagePreferences.saveImageUrls(urls)
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
            val newList = arrayListOf(ListStoryItem(
                name = loginInfo.name, photoUrl = imagePath, description = description
            ))
            listStory?.let { newList.addAll(it) }
            val listStoryAdapter = ListStoryAdapter(newList)
            binding.rvListStory.adapter = listStoryAdapter
            listStory = newList

            saveImageUrls(newList)
        }
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