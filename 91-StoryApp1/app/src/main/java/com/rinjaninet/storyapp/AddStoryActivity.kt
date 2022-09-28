package com.rinjaninet.storyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rinjaninet.storyapp.databinding.ActivityAddStoryBinding

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddStoryUpload.setOnClickListener {
            finish()
        }
    }
}