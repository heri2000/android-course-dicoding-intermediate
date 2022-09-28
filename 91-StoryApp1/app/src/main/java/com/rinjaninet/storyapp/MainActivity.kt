package com.rinjaninet.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rinjaninet.storyapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMainAddNewStory.setOnClickListener {
            val addStoryIntent = Intent(this, AddStoryActivity::class.java)
            startActivity(addStoryIntent)
        }

        val token: String? = intent.getStringExtra(EXTRA_TOKEN)
        if (token == null) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginIntent)
            finish()
        }
    }

    companion object {
        public const val EXTRA_TOKEN = "extra_token"
    }
}