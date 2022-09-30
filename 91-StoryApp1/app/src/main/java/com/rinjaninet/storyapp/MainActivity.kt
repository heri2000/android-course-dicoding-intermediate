package com.rinjaninet.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.rinjaninet.storyapp.addstory.AddStoryActivity
import com.rinjaninet.storyapp.databinding.ActivityMainBinding
import com.rinjaninet.storyapp.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token: String? = intent.getStringExtra(EXTRA_TOKEN)
        if (token == null) navigateToLogin()

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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                navigateToLogin()
            }
        }
        return true
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}