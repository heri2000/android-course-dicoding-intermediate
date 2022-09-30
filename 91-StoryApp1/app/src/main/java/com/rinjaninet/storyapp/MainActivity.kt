package com.rinjaninet.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.rinjaninet.storyapp.addstory.AddStoryActivity
import com.rinjaninet.storyapp.databinding.ActivityMainBinding
import com.rinjaninet.storyapp.login.LoginActivity
import com.rinjaninet.storyapp.login.LoginResult
import com.rinjaninet.storyapp.preferences.LoginPreferences

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var loginInfo: LoginResult
    private lateinit var mLoginPreferences: LoginPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreferences = LoginPreferences(this)
        loginInfo = mLoginPreferences.getLogin()

        if (loginInfo.token.isEmpty()) navigateToLogin()

        binding.btnAddStory.setOnClickListener {
            val addStoryIntent = Intent(this, AddStoryActivity::class.java)
            startActivity(addStoryIntent)
        }
    }

    private fun navigateToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
        finish()
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