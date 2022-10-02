package com.rinjaninet.storyapp.story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
                .into(binding.ivStoryPhoto)
            binding.tvStoryDescription.text = story.description
            title = story.name
        } else {
            title = resources.getString(R.string.no_data)
            binding.ivStoryPhoto.setImageResource(R.drawable.ic_baseline_image_24)
            binding.tvStoryDescription.text = ""
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}