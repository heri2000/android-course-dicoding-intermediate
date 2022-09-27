package com.rinjaninet.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rinjaninet.storyapp.databinding.ActivityListStoryBinding

class ListStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddNewStory.setOnClickListener {
            val addStoryIntent = Intent(this, AddStoryActivity::class.java)
            startActivity(addStoryIntent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val exitIntent = Intent(this, MainActivity::class.java)
        exitIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        exitIntent.putExtra(MainActivity.EXTRA_EXIT, true)
        startActivity(exitIntent)
    }
}