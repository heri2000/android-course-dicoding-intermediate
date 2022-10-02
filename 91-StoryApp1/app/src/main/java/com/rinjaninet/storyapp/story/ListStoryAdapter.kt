package com.rinjaninet.storyapp.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rinjaninet.storyapp.databinding.ItemRowStoryBinding

class ListStoryAdapter(
    private var listStory: ArrayList<ListStoryItem>
): RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    // private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val listStoryItem = listStory[position]
        holder.binding.apply {
            tvStoryItemName.text = listStoryItem.name
            tvStoryItemDescriptionPart.text = listStoryItem.description
            if (listStoryItem.photoUrl != null)
                ivStoryItemPhoto.loadImage(listStoryItem.photoUrl)
        }

        holder.apply {
            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivStoryItemPhoto, "photo"),
                        Pair(binding.tvStoryItemDescriptionPart, "description")
                    )
                val storyIntent = Intent(itemView.context, StoryActivity::class.java)
                storyIntent.putExtra(StoryActivity.EXTRA_STORY, listStoryItem)
                itemView.context.startActivity(storyIntent, optionsCompat.toBundle())
            }
        }

        // holder.itemView.setOnClickListener {
        //     onItemClickCallback.onItemClicked(listStory[holder.adapterPosition])
        // }
    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    private fun ImageView.loadImage(url: String) {
        Glide.with(this.context)
            .load(url)
            .apply(RequestOptions.centerCropTransform())
            .into(this)
    }

    // interface OnItemClickCallback {
    //     fun onItemClicked(data: ListStoryItem)
    // }

    // fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
    //     this.onItemClickCallback = onItemClickCallback
    // }

    class ListViewHolder(var binding: ItemRowStoryBinding): RecyclerView.ViewHolder(binding.root)
}