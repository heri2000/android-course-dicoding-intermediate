package com.rinjaninet.storyapp.story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.rinjaninet.storyapp.R
import com.rinjaninet.storyapp.databinding.ActivityStoryBinding

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story: ListStoryItem? = intent.getParcelableExtra(EXTRA_STORY)
        if (story != null) {
            Glide.with(this)
                .load(story.photoUrl)
                .into(binding.ivDetailPhoto)
            binding.tvDetailDescription.text = story.description
            binding.tvDetailName.text = story.name
            binding.tvDetailLocation.text = if (story.lat == null || story.lon == null) {
                resources.getString(R.string.location_unknown)
            } else {
                resources.getString(R.string.latlon, story.lat, story.lon)
            }
        } else {
            binding.ivDetailPhoto.setImageResource(R.drawable.ic_baseline_image_24)
            binding.tvDetailName.text = ""
            binding.tvDetailLocation.text = ""
            binding.tvDetailDescription.text = ""
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}