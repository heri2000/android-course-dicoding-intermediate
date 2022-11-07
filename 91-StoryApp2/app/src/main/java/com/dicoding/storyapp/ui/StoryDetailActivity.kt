package com.dicoding.storyapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityStoryDetailBinding
import com.dicoding.storyapp.network.ListStoryItem

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story: ListStoryItem? = intent.getParcelableExtra(EXTRA_STORY)
        if (story != null) {
            Glide.with(this)
                .load(story.photoUrl)
                .into(binding.ivDetailPhoto)
            binding.tvDetailName.text = story.name
            binding.tvDetailDescription.text = story.description
            binding.tvDetailLocation.text = if (story.lat == null || story.lon == null) "-"
            else StringBuilder(story.lat.toString()).append(", ").append(story.lon.toString()).toString()
        } else {
            binding.ivDetailPhoto.setImageResource(R.drawable.ic_baseline_image_24)
            binding.tvDetailName.text = ""
            binding.tvDetailDescription.text = ""
            binding.tvDetailLocation.text = ""
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}