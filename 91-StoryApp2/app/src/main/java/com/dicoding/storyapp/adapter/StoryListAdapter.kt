package com.dicoding.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ItemStoryBinding
import com.dicoding.storyapp.network.ListStoryItem
import com.dicoding.storyapp.ui.StoryDetailActivity

class StoryListAdapter :
    PagingDataAdapter<ListStoryItem, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var ivItemPhoto: ImageView =
            itemView.findViewById(R.id.iv_item_photo)
        private var tvItemName: TextView =
            itemView.findViewById(R.id.tv_item_name)
        private var tvItemLocation: TextView =
            itemView.findViewById(R.id.tv_item_location)
        private var tvItemDescription: TextView =
            itemView.findViewById(R.id.tv_item_description)

        fun bind(story: ListStoryItem) {
            story.photoUrl?.let { binding.ivItemPhoto.loadImage(it) }
            binding.tvItemName.text = story.name
            binding.tvItemDescription.text = story.description
            binding.tvItemLocation.text = if (story.lat == null || story.lon == null) "-"
            else StringBuilder(story.lat.toString())
                .append(", ")
                .append(story.lon.toString())
                .toString()

            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(ivItemPhoto, "photo"),
                        Pair(tvItemName, "name"),
                        Pair(tvItemLocation, "location"),
                        Pair(tvItemDescription, "description")
                    )
                val storyIntent = Intent(itemView.context, StoryDetailActivity::class.java)
                storyIntent.putExtra(StoryDetailActivity.EXTRA_STORY, story)
                itemView.context.startActivity(storyIntent, optionsCompat.toBundle())
            }
        }

        private fun ImageView.loadImage(url: String) {
            Glide.with(this.context)
                .load(url)
                .apply(RequestOptions.centerCropTransform())
                .into(this)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}